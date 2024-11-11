package com.example.alog.entity;

import jakarta.persistence.Table;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id", nullable = false, unique = true)
    private Long issueId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255, columnDefinition = "VARCHAR(255) DEFAULT '기타'")
    private String category = "기타";

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime date;

    @Column(nullable = false, length = 255)
    private String status = "진행중";

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean verified = false;

    @Column(nullable = false, length = 255)
    private String addr;

    // 기본 생성자
    public Issue() {}

    // 생성자
    public Issue(String title, String category, String description, BigDecimal latitude, BigDecimal longitude, String addr) {
        this.title = title;
        this.category = category != null ? category : "기타";
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = LocalDateTime.now();
        this.status = "진행중";
        this.verified = false;
        this.addr = addr;
    }

    // @PrePersist 사용하여 엔티티 저장 전 date 필드 자동 설정
    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }

    // Getter와 Setter 추가
    public Long getId() {
        return issueId;
    }

    public void setId(Long id) {
        this.issueId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
