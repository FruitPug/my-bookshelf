package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.notification.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe() {
        return subscriptionService.subscribe();
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe() {
        return subscriptionService.unsubscribe();
    }
}

