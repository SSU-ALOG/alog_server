package com.example.alog.controller;

import com.example.alog.entity.ShortVideo;
import com.example.alog.service.ShortVideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-videos")
public class ShortVideoController {

    private final ShortVideoService shortVideoService;

    public ShortVideoController(ShortVideoService shortVideoService) {
        this.shortVideoService = shortVideoService;
    }

    // 특정 issue_id에 해당하는 영상 조회
    @GetMapping("/{issueId}")
    public ResponseEntity<List<ShortVideo>> getVideosByIssueId(@PathVariable Long issueId) {
        try {
            List<ShortVideo> videos = shortVideoService.getVideosByIssueId(issueId);
            if (videos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
