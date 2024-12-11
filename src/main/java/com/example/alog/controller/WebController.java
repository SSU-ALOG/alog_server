package com.example.alog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/create-issue")
    public String createIssuePage() {
        return "create-issue";
    }

}
