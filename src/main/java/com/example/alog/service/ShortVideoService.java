package com.example.alog.service;

import com.example.alog.entity.Issue;
import com.example.alog.entity.ShortVideo;
import com.example.alog.repository.ShortVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShortVideoService {

    private final ShortVideoRepository shortVideoRepository;

    public ShortVideoService(ShortVideoRepository shortVideoRepository) {
        this.shortVideoRepository = shortVideoRepository;
    }

    // 특정 issue_id에 해당하는 모든 ShortVideo 조회
    public List<ShortVideo> getVideosByIssueId(Long issueId) {
        List<ShortVideo> shorts = shortVideoRepository.findByIssueId(issueId);
        System.out.println("Issues within a week: " + shorts);
        return shorts;
    }
}
