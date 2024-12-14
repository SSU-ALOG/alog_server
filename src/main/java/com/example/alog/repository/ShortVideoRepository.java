package com.example.alog.repository;

import com.example.alog.entity.Issue;
import com.example.alog.entity.ShortVideo;
import com.example.alog.entity.ShortVideoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShortVideoRepository extends JpaRepository<ShortVideo, ShortVideoId> {

    // 특정 issue_id에 해당하는 모든 ShortVideo 조회
    @Query("SELECT i FROM ShortVideo i WHERE i.issueId = :issueId")
    List<ShortVideo> findByIssueId(@Param("issueId") Long issueId);
}
