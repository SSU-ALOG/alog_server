package com.example.alog.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FCMConfig {

    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase/firebase-adminsdk.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase 초기화 완료");
        } catch (IOException e) {
            System.err.println("Firebase 초기화 실패: " + e.getMessage());
        }
    }
}
