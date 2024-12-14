package com.example.alog.controller;

import com.example.alog.entity.Issue;
import com.example.alog.entity.Message;
import com.example.alog.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Message>> getRecentMessages() {
        return ResponseEntity.ok(messageService.getMessagesWithinAWeek());
    }
}
