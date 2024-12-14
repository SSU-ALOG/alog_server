package com.example.alog.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Configuration
public class FCMConfig {

    @PostConstruct
    public void initialize() {
        try {
            // 로컬용 (window)
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase/firebase-adminsdk.json");

            // 서버 배포용 (linux)
//            Resource resource = new ClassPathResource("firebase/firebase-adminsdk.json");
//            InputStream serviceAccount = resource.getInputStream();

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
