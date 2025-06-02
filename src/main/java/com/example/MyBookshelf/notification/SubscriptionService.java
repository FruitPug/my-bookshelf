package com.example.MyBookshelf.notification;

import com.example.MyBookshelf.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriptionService {
    private final Set<Long> subscribers = ConcurrentHashMap.newKeySet();
    private final CurrentUserService currentUserService;

    public SubscriptionService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public ResponseEntity<Void> subscribe() {
        Long userId = currentUserService.get().getId();
        subscribers.add(userId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> unsubscribe() {
        Long userId = currentUserService.get().getId();
        subscribers.remove(userId);
        return ResponseEntity.ok().build();
    }

    public boolean isSubscribed(Long userId) {
        return subscribers.contains(userId);
    }
}

