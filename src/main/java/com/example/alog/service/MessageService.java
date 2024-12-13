package com.example.alog.service;

import com.example.alog.entity.IssueMetaData;
import com.example.alog.entity.Message;
import com.example.alog.repository.IssueMetaDataRepository;
import com.example.alog.repository.MessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // 데이터 처리 및 DB 저장
    public void saveMessages(Optional<IssueMetaData> metaData) throws Exception {
        String jsonResponse = callAPI();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        JsonNode bodyArray = rootNode.get("body");

        if (bodyArray != null && bodyArray.isArray()) {
            LocalDateTime lastCallTime = LocalDateTime.parse(metaData.map(meta -> String.valueOf(meta.getDatetime()).replace("Z", "")).orElse(null));

            System.out.println("MessageService: lastCallTime: " + lastCallTime);

            List<Message> messages = new ArrayList<>();

            for (JsonNode messageNode : bodyArray) {
                String crtDtStr = messageNode.get("CRT_DT").asText().replace("/", "-");
                LocalDateTime crtDt = LocalDateTime.parse(crtDtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

//                System.out.println("MessageService: crtDt: " + crtDt);

                // 메시지 생성 시간이 lastCallTime 이후인 경우만 처리
                if (crtDt.isAfter(lastCallTime)) {
                    Message message = new Message();
                    message.setMsgSn(messageNode.get("SN").asLong());
                    message.setDstSeNm(messageNode.get("DST_SE_NM").asText());
                    message.setCrtDt(Timestamp.valueOf(crtDt));
                    message.setMsgCn(messageNode.get("MSG_CN").asText());
                    message.setRcptnRgnNm(messageNode.get("RCPTN_RGN_NM").asText());
                    message.setEmrgStepNm(messageNode.get("EMRG_STEP_NM").asText());

                    messages.add(message);

                    System.out.println("MessageService: message: " + message.getMsgCn() + " / " + crtDt);
                }
            }

            // DB에 저장
//            if (!messages.isEmpty()) {
//                messageRepository.saveAll(messages);
//            }
        }
    }

    // API 호출
    private String callAPI() throws Exception {
//        String serviceKey = "서비스키";
//
//        // 조회시작일자(YYYYMMDD)로 오늘 일자 가져오기
//        LocalDate today = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String crtDt = today.format(formatter);
//
//        /* API를 호출하기 위한 URL 생성 */
//        StringBuilder urlBuilder = new StringBuilder("https://www.safetydata.go.kr/V2/api/");
//        urlBuilder.append("?" + "serviceKey=" + serviceKey);
//        urlBuilder.append("&" + "crtDt=" + crtDt);
//
//        URI uri = new URI(urlBuilder.toString());
//        URL url = uri.toURL();
//
//        /* API 호출하기 위한 HTTP 커넥션과 리더 생성 */
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//
//        BufferedReader reader;
//
//        /* API 호출 */
//        connection.connect();
//        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
//            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        } else {
//            throw new RuntimeException("Connection failed with status code: " + connection.getResponseCode());
//        }
//
//        /* API 응답에서 데이터 추출 */
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            sb.append(line);
//        }
//
//        reader.close();
//        connection.disconnect();
//
//        return sb.toString();
        
        // 데이터 없는 경우 처리도 추가해야 함
        return "{\"header\":{\"resultMsg\":\"NORMAL SERVICE\",\"resultCode\":\"00\",\"errorMsg\":null},\"numOfRows\":10,\"pageNo\":1,\"totalCount\":22452,\"body\":[{\"MSG_CN\":\"[행정안전부] 오늘 11시10분 부산 호우경보, 산사태ㆍ상습침수 등 위험지역 대피, 외출자제 등 안전에 주의바랍니다\",\"RCPTN_RGN_NM\":\"부산광역시 전체 \",\"CRT_DT\":\"2023/09/16 11:09:49\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205163,\"DST_SE_NM\":\"호우\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[울주군]오늘부터 내일까지 많은 비가 예상되고 있습니다. 하천변 산책로, 계곡, 저지대, 산 인접지 등 위험 지역에는 접근 삼가해주시기 바랍니다.\",\"RCPTN_RGN_NM\":\"울산광역시 울주군 \",\"CRT_DT\":\"2023/09/16 11:11:56\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205164,\"DST_SE_NM\":\"호우\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[부산광역시] 오늘 11시 10분 부산지역 호우경보. 저지대, 침수 우려지역 등 위험지역에서는 가족, 이웃과 정보를 공유하고 안전한 곳으로 대피하시기 바랍니다\",\"RCPTN_RGN_NM\":\"부산광역시 전체 \",\"CRT_DT\":\"2023/09/16 11:12:53\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205165,\"DST_SE_NM\":\"호우\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[진도군]10:50 호우주의보 발효. 내일까지 많은 비가 예상되니 외출자제 및 위험지역(하천변, 산사태 등) 접근 금지, 위험 징후 발견 시 즉시 대피 바랍니다.\",\"RCPTN_RGN_NM\":\"전라남도 진도군 \",\"CRT_DT\":\"2023/09/16 11:14:58\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205166,\"DST_SE_NM\":\"호우\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[경상남도] 남해고속도로 진성IC 인근 부산방향의 화물차 사고로, 양방향 극심한 정체중이니 국도우회하여 주시기 바랍니다\",\"RCPTN_RGN_NM\":\"경상남도 전체 \",\"CRT_DT\":\"2023/09/16 11:22:36\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205167,\"DST_SE_NM\":\"교통통제\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[한국수자원공사] 9월 16일 15시부터 주암댐 초당 200톤 이내 수문방류로 하류지역 하천수위 상승 예상. 하천주변에 계신 경우 안전한 곳으로 대피 바랍니다.\",\"RCPTN_RGN_NM\":\"경상남도 하동군 ,전라남도 곡성군 ,전라남도 광양시 ,전라남도 구례군 ,전라남도 순천시 \",\"CRT_DT\":\"2023/09/16 11:25:08\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205168,\"DST_SE_NM\":\"기타\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[양산시청] 오늘 10시 30분 호우주의보 발효중. 하천변산책로, 산사태위험지와 저지대 침수우려지역 접근금지 및 지하차도 운행시 안전에 주의하시기 바랍니다.\",\"RCPTN_RGN_NM\":\"경상남도 양산시 \",\"CRT_DT\":\"2023/09/16 11:28:58\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205169,\"DST_SE_NM\":\"호우\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[상주시] 상주지역에 호우예비특보발표 밤사이 많은 비 예보. 외출자재, 하천변 접근금지, 급경사지 인근 및 산사태 위험지역 근처 주민은 안전한 곳으로 대피\",\"RCPTN_RGN_NM\":\"경상북도 상주시 \",\"CRT_DT\":\"2023/09/16 11:32:21\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205170,\"DST_SE_NM\":\"호우\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[경기남부경찰청]부천시에서 실종된 현명란씨(여,53세)를 찾습니다-163cm,60kg,검정반팔(어깨,허리부분 노란색),검정바지 vo.la/t5Fpt/ ☎182\",\"RCPTN_RGN_NM\":\"경기도 부천시 \",\"CRT_DT\":\"2023/09/16 11:33:44\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205171,\"DST_SE_NM\":\"기타\",\"MDFCN_YMD\":\"2023-09-16\"},{\"MSG_CN\":\"[김해시] 김해시 호우주의보 발효중으로 특히 서부지역(진례 등) 많은 비가 내렸으니 지하차도 통행 자제, 농배수로 물꼬작업 등 위험지역 접근 삼가해주시기바랍니다.\",\"RCPTN_RGN_NM\":\"경상남도 김해시 \",\"CRT_DT\":\"2023/09/16 11:41:41\",\"REG_YMD\":\"2023-09-16\",\"EMRG_STEP_NM\":\"안전안내\",\"SN\":205172,\"DST_SE_NM\":\"호우\",\"MDFCN_YMD\":\"2023-09-16\"}]}";
    }
}
