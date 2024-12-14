package com.example.alog.scheduler;

import com.example.alog.entity.IssueMetaData;
import com.example.alog.repository.IssueMetaDataRepository;
import com.example.alog.service.MessageService;
import com.example.alog.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScheduledTask {

    private final MessageService messageService;
    private final WebCrawlerService webCrawlerService;

    @Autowired
    IssueMetaDataRepository metaDataRepository;

    public ScheduledTask(MessageService messageService, WebCrawlerService webCrawlerService) {
        this.messageService = messageService;
        this.webCrawlerService = webCrawlerService;
    }

    @Scheduled(fixedRate = 3000000)
//    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void runCrawler() throws Exception {
        Optional<IssueMetaData> metaData = metaDataRepository.findById(1L);
        webCrawlerService.fetchAndSaveNewData(metaData);
        System.out.println("크롤링 작업 완료");
        messageService.saveMessages(metaData);
    }
}
