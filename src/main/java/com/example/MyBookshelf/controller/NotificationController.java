package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.notification.SubscriptionService;
import com.example.MyBookshelf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final SubscriptionService subs;
    private final UserRepository users;

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(Authentication auth) {
        Long id = users.findByEmail(auth.getName()).get().getId();
        subs.subscribe(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(Authentication auth) {
        Long id = users.findByEmail(auth.getName()).get().getId();
        subs.unsubscribe(id);
        return ResponseEntity.ok().build();
    }
}

