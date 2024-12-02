package com.example.alog.service;

import com.example.alog.entity.Issue;
import com.example.alog.entity.IssueMetaData;
import com.example.alog.repository.IssueMetaDataRepository;
import com.example.alog.repository.IssueRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class WebCrawlerService {

    @Autowired
    private IssueRepository repository;

    @Autowired
    private IssueMetaDataRepository metaDataRepository;

    private WebDriver driver;

//    @Value("${naver.api.url")
    private String NAVER_API_URL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc";

    @Value("${naver.api.id}")
    private String CLIENT_ID;

    @Value("${naver.api.secret}")
    private String CLIENT_SECRET;

    public WebCrawlerService() throws IOException {
        String chromeDriverPath = new ClassPathResource("chromedriver-win64/chromedriver.exe").getFile().getAbsolutePath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
//        options.addArguments("--disable-gpu", "--no-sandbox");
        this.driver = new ChromeDriver(options);
    }

    public void fetchAndSaveNewData() {
        try {
            // 최근 크롤링 시간 가져오기
            Optional<IssueMetaData> metadata = metaDataRepository.findById(1L);
            String lastCrawlTime = metadata.map(meta -> String.valueOf(meta.getDatetime())).orElse(null);

//            System.out.println("lastCrawlTime: " + lastCrawlTime);

            // 대상 페이지 접속
            driver.get("https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/fcl/traficInfoList.html?menuSeq=881");

            List<Issue> newDataList = new ArrayList<>();
            boolean hasNewData = false;

            getTrafficData(lastCrawlTime);

            // 새로운 데이터를 DB에 저장
            if (hasNewData) {
                repository.saveAll(newDataList);

                // 최근 크롤링 시간 업데이트
                IssueMetaData issueMetaData = metadata.orElse(new IssueMetaData());
                issueMetaData.setDatetime(Instant.now());
                metaDataRepository.save(issueMetaData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private List<Issue> getTrafficData(String lastCrawlTime) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<Issue> dataList = new ArrayList<>();
        WebElement apiTrElement = null;

        // 대상 페이지 접속
        driver.get("https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/fcl/traficInfoList.html?menuSeq=881");

        try {
            for (int category = 1; category < 4; category++) {
                // 검색구분 선택
                Select dropdown = new Select(driver.findElement(By.id("sbArea2")));
                dropdown.selectByValue(String.valueOf(category));

                // 검색 클릭
                WebElement searchButton = driver.findElement(By.cssSelector("a.search_btn"));
                searchButton.click();

                // 페이지 로드 대기
//                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#apiTr > tr[id*='_apiData1']")));
                Thread.sleep(2000);

                System.out.println("category: " + (category == 1 ? "사고·고장" : category == 2 ? "공사" : "행사"));

                while (true) {
                    try {
                        // 현재 페이지 데이터 행 대기
//                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#apiTr > tr[id*='_apiData1']")));

//                        List<WebElement> rows = driver.findElements(By.cssSelector("#apiTr > tr[id*='_apiData1']"));

                        apiTrElement = driver.findElement(By.cssSelector("#apiTr"));
                        List<WebElement> rows = apiTrElement.findElements(By.cssSelector("tr[id*='_apiData1']"));
                        int rowCount = rows.size();

                        System.out.println("rowCount: " + rowCount);

                        rowCount = 0;
                        for (int i = 0; i < rowCount; i++) {
                            while (true) {
                                try {
                                    // 행 데이터 및 id 값 가져오기
//                                    WebElement row = driver.findElements(By.cssSelector("#apiTr > tr[id*='_apiData1']")).get(i);
                                    WebElement row = rows.get(i);
                                    String rowId = row.getAttribute("id");
                                    String idx = rowId.split("_")[1];

                                    // typeOther 및 updateTime 데이터 가져오기
                                    String typeOther = row.findElement(By.cssSelector("span[id$='_typeOther']")).getText();
                                    String updateTime = row.findElement(By.cssSelector("span[id$='_updateTime']")).getText();

                                    // 지도창 호출
                                    JavascriptExecutor js = (JavascriptExecutor) driver;
                                    js.executeScript("apiView(" + idx + ");");

                                    // 새 창 대기 및 전환
                                    String mainWindowHandle = driver.getWindowHandle();
                                    Set<String> windowHandles = driver.getWindowHandles();
                                    for (String handle : windowHandles) {
                                        if (!handle.equals(mainWindowHandle)) {
                                            driver.switchTo().window(handle);
                                            break;
                                        }
                                    }

                                    // URL에서 위도와 경도 추출
                                    String popupUrl = driver.getCurrentUrl();
                                    double longitude = Double.parseDouble(extractQueryParam(popupUrl, "lo"));
                                    double latitude = Double.parseDouble(extractQueryParam(popupUrl, "la"));
                                    String addr = getAddress(longitude, latitude);

                                    // 새 창 닫기 및 원래 창으로 복귀
                                    driver.close();
                                    driver.switchTo().window(mainWindowHandle);

                                    // 업데이트 시간 비교 및 데이터 처리
//                                    if (lastCrawlTime == null || updateTime.compareTo(lastCrawlTime) > 0) {
//                                        Issue newIssue = new Issue();
//                                        newIssue.setContent(content);
//                                        newIssue.setUpdateTime(updateTime);
//                                        newDataList.add(newIssue);
//                                        hasNewData = true;
//                                    }

                                    System.out.println(typeOther + " " + updateTime + " (" + latitude + ", " + longitude + ")" + " " + addr);

                                    break;

                                } catch (Exception e) {
                                    // 오류 발생 시 재시도
//                                   System.out.println("데이터 처리 중 오류 발생: " + e.getMessage());

                                    // 재시도 전에 대기 시간 (너무 빈번한 시도 방지)
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }
                        }

                        // 현재 페이지 번호와 최대 페이지 번호 비교
                        int minPage = Integer.parseInt(driver.findElement(By.id("minPage")).getText().trim());
                        int maxPage = Integer.parseInt(driver.findElement(By.id("maxPage")).getText().trim());

                        if (minPage >= maxPage) {
                            break; // 마지막 페이지인 경우 종료
                        } else {
                            // 다음 페이지로 이동
                            WebElement nextButton = driver.findElement(By.cssSelector("#content > div.tabContent_boxWrap > div.boardList_boxWrap > div > div > a:nth-child(3)"));
                            nextButton.click();

                            // 페이지 로드 대기
                            wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
                            wait.until(ExpectedConditions.textToBe(By.id("minPage"), String.valueOf(minPage + 1)));

                        }
                    } catch (StaleElementReferenceException e) {
                        System.out.println("StaleElementReferenceException 발생: 재시도 중...");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("검색 실행 중 오류 발생: " + e.getMessage());
        }

        return dataList;
    }

    // 좌표 주소 변환
    public String getAddress(double longitude, double latitude) {
        // 요청 URL 구성
        String url = String.format("%s?coords=%f,%f&output=json&orders=addr", NAVER_API_URL, longitude, latitude);

        // HTTP 요청 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        headers.set("X-NCP-APIGW-API-KEY", CLIENT_SECRET);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 요청 전송
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        // JSON 응답에서 주소 정보 추출
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");

                if (!results.isEmpty()) {
                    Map<String, Object> region = (Map<String, Object>) results.get(0).get("region");
                    String area1 = ((Map<String, Object>) region.get("area1")).get("name").toString();

                    return String.format("%s", area1);
                }
            }
        }
        throw new RuntimeException("Failed to retrieve city address from Naver Map API");
    }

    // URL에서 좌표 가져오기
    private String extractQueryParam(String url, String param) {
        try {
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                String[] params = query.split("&");
                for (String p : params) {
                    String[] keyValue = p.split("=");
                    if (keyValue.length == 2 && keyValue[0].equals(param)) {
                        return keyValue[1];
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("쿼리 파라미터 추출 중 오류 발생: " + e.getMessage());
        }
        return null;
    }

    public List<String> fetchTableData() throws IOException {
        List<String> data = new ArrayList<>();

        // ChromeDriver 경로 설정
        String chromeDriverPath = new ClassPathResource("chromedriver-win64/chromedriver.exe").getFile().getAbsolutePath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 브라우저 창을 열지 않음
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        // WebDriver 생성
        WebDriver driver = new ChromeDriver(options);

        try {
            // 대상 URL 접속
            String url = "https://nfds.go.kr/dashboard/monitor.do";
            driver.get(url);

            // 페이지 로드 대기 (필요에 따라 조정)
            Thread.sleep(5000);

            // 페이지 소스 가져오기
            String pageSource = driver.getPageSource();

            // Jsoup을 사용하여 Document 파싱
            Document doc = Jsoup.parse(pageSource);

            // element 선택
            Element tbody = doc.selectFirst("#grdViewer > table > tbody");
            if (tbody != null) {
                // tbody 내부의 각 행을 순회
                Elements rows = tbody.select("tr");
                for (Element row : rows) {
                    // 각 행의 셀 데이터 추출
                    Elements cells = row.select("td");
                    StringBuilder rowData = new StringBuilder();
                    for (Element cell : cells) {
                        rowData.append(cell.text()).append(" | ");
                    }
                    data.add(rowData.toString());
                    System.out.println("크롤링된 행 데이터: " + rowData);
                }
            } else {
                System.out.println("지정된 셀렉터에 해당하는 데이터가 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();
        }

        return data;
    }
}
