package com.example.alog.controller;

import com.example.alog.service.WebCrawlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class WebCrawlerController {

    private final WebCrawlerService webCrawlerService;

    public WebCrawlerController(WebCrawlerService webCrawlerService) {
        this.webCrawlerService = webCrawlerService;
    }

//    @GetMapping("/crawl-table")
//    public List<String> crawlTable() throws IOException {
//        return webCrawlerService.fetchTableData();
//    }
}
