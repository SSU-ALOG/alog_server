package com.example.alog.service;

import com.example.alog.entity.Issue;
import com.example.alog.entity.IssueMetaData;
import com.example.alog.repository.IssueMetaDataRepository;
import com.example.alog.repository.IssueRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WebCrawlerService {

    @Autowired
    private IssueRepository repository;

    @Autowired
    private IssueMetaDataRepository metaDataRepository;

    private WebDriver driver;

    public WebCrawlerService() throws IOException {
        // ChromeDriver 설정
        String chromeDriverPath = new ClassPathResource("chromedriver-win64/chromedriver.exe").getFile().getAbsolutePath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
        this.driver = new ChromeDriver(options);
    }

    public void fetchAndSaveNewData() {
        try {
            // 최근 크롤링 시간 가져오기
            Optional<IssueMetaData> metadata = metaDataRepository.findById(Long.valueOf(1));
            String lastCrawlTime = String.valueOf(metadata.get().getDatetime()); // 최근 크롤링 시간
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 대상 페이지 접속
            driver.get("https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/fcl/traficInfoList.html?menuSeq=881");
            Thread.sleep(3000); // 페이지 로드 대기

            // 페이지 크롤링
            List<Issue> newDataList = new ArrayList<>();
            boolean hasNewData = false;

            while (true) {
                // 현재 페이지의 데이터 행들 가져오기
                List<WebElement> rows = driver.findElements(By.cssSelector("#apiTr"));
                for (WebElement row : rows) {
                    // 데이터와 업데이트 시간 가져오기
                    String content = row.getText();
                    String updateTime = row.findElement(By.cssSelector("#apiTr_0_updateTime")).getText();

                    // 출력하기
                    System.out.println("행 내용: " + row.getText());

                    // 업데이트 시간을 비교하여 새 데이터만 처리
//                    if (lastCrawlTime == null || dateFormat.parse(updateTime).after(dateFormat.parse(lastCrawlTime))) {
//                        String uniqueKey = String.valueOf(content.hashCode());
//                        Issue newData = new Issue(content);
//                        newDataList.add(newData);
//                        hasNewData = true;
//                    }
                }

                // 다음 페이지 버튼 확인 및 이동
                WebElement nextButton = driver.findElement(By.cssSelector("#content > div.tabContent_boxWrap > div.boardList_boxWrap > div > div > a:nth-child(3)"));
                if (nextButton.getAttribute("class").contains("disabled")) {
                    break; // 다음 페이지가 없으면 종료
                } else {
                    nextButton.click();
                    Thread.sleep(2000); // 페이지 로드 대기
                }
            }

            // 데이터 저장
            if (hasNewData) {
                repository.saveAll(newDataList);
                System.out.println("새 데이터 저장 완료: " + newDataList.size() + "개");

                // 최근 크롤링 시간 업데이트
                IssueMetaData issueMetaData = metadata.get();
                issueMetaData.setDatetime(Instant.now());
                metaDataRepository.save(issueMetaData);
            } else {
                System.out.println("새로운 데이터가 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
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
