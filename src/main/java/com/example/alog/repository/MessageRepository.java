package com.example.alog.repository;

import com.example.alog.entity.Issue;
import com.example.alog.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.crtDt >= :startDate")
    List<Message> findMessagesWithinAWeek(@Param("startDate") LocalDateTime startDate);
}