package com.example.alog.entity;

import java.io.Serializable;
import java.util.Objects;

public class ShortVideoId implements Serializable {

    private Long issueId; // 복합 키의 첫 번째 필드
    private String link;  // 복합 키의 두 번째 필드

    // 기본 생성자 (필수)
    public ShortVideoId() {}

    // 모든 필드를 포함한 생성자
    public ShortVideoId(Long issueId, String link) {
        this.issueId = issueId;
        this.link = link;
    }

    // equals 메서드 오버라이드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortVideoId that = (ShortVideoId) o;
        return Objects.equals(issueId, that.issueId) && Objects.equals(link, that.link);
    }

    // hashCode 메서드 오버라이드
    @Override
    public int hashCode() {
        return Objects.hash(issueId, link);
    }

    // Getter와 Setter
    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
