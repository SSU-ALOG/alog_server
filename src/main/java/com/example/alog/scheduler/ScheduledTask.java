package com.example.alog.scheduler;

import com.example.alog.service.WebCrawlerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    private final WebCrawlerService webCrawlerService;

    public ScheduledTask(WebCrawlerService webCrawlerService) {
        this.webCrawlerService = webCrawlerService;
    }

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void runCrawler() {
        webCrawlerService.fetchAndSaveNewData();
        System.out.println("크롤링 작업 완료");
    }
}
