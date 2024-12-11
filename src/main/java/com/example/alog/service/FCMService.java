package com.example.alog.service;

import com.example.alog.entity.Issue;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    /**
     * FCM 메시지 전송 (데이터 전체 포함)
     *
     * @param issue Issue 데이터
     */
    public void sendNotificationWithData(Issue issue) {
        try {
            // 데이터 payload 설정
            Message message = Message.builder()
                    .putData("issueId", String.valueOf(issue.getId()))
                    .putData("title", issue.getTitle())
                    .putData("category", issue.getCategory())
                    .putData("description", issue.getDescription() != null ? issue.getDescription() : "")
                    .putData("latitude", String.valueOf(issue.getLatitude()))
                    .putData("longitude", String.valueOf(issue.getLongitude()))
                    .putData("date", issue.getDate().toString())
                    .putData("status", issue.getStatus())
                    .putData("verified", String.valueOf(issue.getVerified()))
                    .putData("addr", issue.getAddr())
                    .setTopic("alog-all") // 모든 사용자에게 전송
                    .build();

            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 메시지 전송 성공: " + response);
        } catch (Exception e) {
            System.err.println("FCM 메시지 전송 실패: " + e.getMessage());
        }
    }
}
