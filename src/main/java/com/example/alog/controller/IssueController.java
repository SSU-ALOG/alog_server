package com.example.alog.controller;

import com.example.alog.entity.Issue;
import com.example.alog.service.IssueService;
import com.example.alog.service.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private FCMService fcmService;

    @PostMapping("/create")
    public ResponseEntity<?> createIssue(@RequestBody Issue issue) {
        if (issue.getAddr() == null || issue.getAddr().isEmpty()) {
            return ResponseEntity.badRequest().body("Address (addr) cannot be null or empty");
        }

        // Issue 저장
        Issue savedIssue = issueService.saveIssue(issue);

        // 사용자에게 Issue 데이터를 포함한 FCM 알림 전송
        fcmService.sendNotificationWithData(savedIssue);

        return ResponseEntity.ok(savedIssue);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Issue>> getRecentIssues() {
        return ResponseEntity.ok(issueService.getIssuesWithinAWeek());
    }
}
