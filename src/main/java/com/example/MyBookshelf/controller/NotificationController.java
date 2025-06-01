package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.notification.SubscriptionService;
import com.example.MyBookshelf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.util.Optional;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final SubscriptionService subs;
    private final UserService userService;

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(Authentication auth) {
        Optional<UserEntity> optionalUser = userService.findByEmail(auth.getName());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long id = optionalUser.get().getId();

        subs.subscribe(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(Authentication auth) {
        Optional<UserEntity> optionalUser = userService.findByEmail(auth.getName());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long id = optionalUser.get().getId();

        subs.unsubscribe(id);
        return ResponseEntity.ok().build();
    }
}

