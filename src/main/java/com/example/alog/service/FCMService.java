package com.example.alog.service;

import com.example.alog.entity.Issue;
import com.google.firebase.messaging.*;
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
                    .setNotification(Notification.builder()
                            .setTitle("[" + issue.getAddr() + "] " + issue.getCategory() + " 알림! 📢")
                            .setBody(issue.getTitle())
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setChannelId("high_importance_channel")
                                    .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                    .build())
                            .build())
                    .build();

            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 메시지 전송 성공: " + response);
        } catch (Exception e) {
            System.err.println("FCM 메시지 전송 실패: " + e.getMessage());
        }
    }
}
