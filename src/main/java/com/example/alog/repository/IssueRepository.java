// db와 상호작용하는 interface

package com.example.alog.repository;

import com.example.alog.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Query("SELECT i FROM Issue i WHERE i.date >= :startDate")
    List<Issue> findIssuesWithinAWeek(@Param("startDate") LocalDateTime startDate);

}
