// spring boot 에서 rest api endpoint 설정하여 android 앱에서 데이터를 전송할 수 있게 함.

package com.example.alog.controller;

import com.example.alog.entity.Issue;
import com.example.alog.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public IssueController(IssueService issueService){
        this.issueService = issueService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createIssue(@RequestBody Issue issue) {
        if (issue.getAddr() == null || issue.getAddr().isEmpty()) {
            return ResponseEntity.badRequest().body("Address (addr) cannot be null or empty");
        }

        Issue savedIssue = issueService.saveIssue(issue);
        messagingTemplate.convertAndSend("/topic/newData", issue);
        return ResponseEntity.ok(savedIssue);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Issue>> getRecentIssues() {
        return ResponseEntity.ok(issueService.getIssuesWithinAWeek());
    }
}
