// 비즈니스 로직 처리

package com.example.alog.service;

import com.example.alog.entity.Issue;
import com.example.alog.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository){ this.issueRepository = issueRepository; }

    public Issue saveIssue(Issue issue) { return issueRepository.save(issue); }

    public List<Issue> getIssuesWithinAWeek(){
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        // 일주일 전 날짜를 구하고 IssueRepository.java의 findIssuesWithinAWeek의 startDate로 전달
        List<Issue> issues = issueRepository.findIssuesWithinAWeek(oneWeekAgo);
        System.out.println("Issues within a week: " + issues);
        return issues;

//        return issueRepository.findIssuesWithinAWeek(oneWeekAgo);
    }
}
