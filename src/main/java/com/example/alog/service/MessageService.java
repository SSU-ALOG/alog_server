package com.example.alog.service;

import com.example.alog.entity.IssueMetaData;
import com.example.alog.entity.Message;
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

    public List<Message> getMessagesWithinAWeek() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Message> messages = messageRepository.findMessagesWithinAWeek(oneWeekAgo);

        if (messages.isEmpty()) {
            System.out.println("No messages found within the last week.");
        } else {
            System.out.println("Message within a week: " + messages);
            messages.forEach(message ->
                    System.out.println("Message ID: " + message.getMsgSn() + ", Content: " + message.getMsgCn())
            );
        }

        return messages;
    }

    // 데이터 처리 및 DB 저장
    public void saveMessages(Optional<IssueMetaData> metaData) throws Exception {
        String jsonResponse = callAPI();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        JsonNode bodyArray = rootNode.get("body");

        // "body"가 null인 경우 처리
        if (bodyArray == null || bodyArray.isNull()) {
            System.out.println("MessageService: No data in API response (body is null). Skipping DB update.");
            return; // "body"가 null이므로 데이터베이스 업데이트 생략
        }

        if (bodyArray != null && bodyArray.isArray()) {
            LocalDateTime lastCallTime = LocalDateTime.parse(metaData.map(meta -> String.valueOf(meta.getDatetime()).replace("Z", "")).orElse(null));

            System.out.println("MessageService: lastCallTime: " + lastCallTime);

            List<Message> messages = new ArrayList<>();

            for (JsonNode messageNode : bodyArray) {
                String crtDtStr = messageNode.get("CRT_DT").asText().replace("/", "-");
                LocalDateTime crtDt = LocalDateTime.parse(crtDtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                System.out.println("MessageService: crtDt: " + crtDt);

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
            if (!messages.isEmpty()) {
                messageRepository.saveAll(messages);
            }
        }
    }

    // API 호출
    private String callAPI() throws Exception {
        String serviceKey = "OB8EMZ06H87S66AR";

        // 조회시작일자(YYYYMMDD)로 오늘 일자 가져오기
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String crtDt = today.format(formatter);

        // URL 생성
        StringBuilder urlBuilder = new StringBuilder("https://www.safetydata.go.kr/V2/api/DSSP-IF-00247");
        urlBuilder.append("?serviceKey=").append(serviceKey);
        urlBuilder.append("&crtDt=").append(crtDt);

        // 로그 추가
        System.out.println("==== Start callAPI ====");
        System.out.println("Generated API URL: " + urlBuilder);
        System.out.println("==== End callAPI ====");

        // 기존 로직 유지
        URI uri = new URI(urlBuilder.toString());
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.connect();
        int statusCode = connection.getResponseCode();

        if (statusCode == 404) {
            throw new RuntimeException("API endpoint not found: " + urlBuilder);
        } else if (statusCode >= 200 && statusCode < 300) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                System.out.println("API Response: " + sb.toString());
                return sb.toString();
            }
        } else {
            throw new RuntimeException("Unexpected HTTP status code: " + statusCode + " for URL: " + urlBuilder);
        }

    }
}