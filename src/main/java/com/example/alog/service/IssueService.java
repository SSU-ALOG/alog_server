// 비즈니스 로직 처리

package com.example.alog.service;

import com.example.alog.entity.Issue;
import com.example.alog.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    public Issue saveIssue(Issue issue) {
        return issueRepository.save(issue);
    }
}
