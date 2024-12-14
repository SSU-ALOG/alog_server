package com.example.alog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(ShortVideoId.class) // 복합 키 클래스 설정
@Table(name = "short_video")
public class ShortVideo {

    @Id
    @Column(name = "issue_id", nullable = false)
    private Long issueId; // 복합 키의 첫 번째 필드

    @Id
    @Column(name = "link", nullable = false, length = 255)
    private String link; // 복합 키의 두 번째 필드

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt; // 생성 시간
}
