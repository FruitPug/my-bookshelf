package com.example.MyBookshelf.notification;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriptionService {
    private final Set<Long> subscribers = ConcurrentHashMap.newKeySet();

    public void subscribe(Long userId)   { subscribers.add(userId); }

    public void unsubscribe(Long userId) { subscribers.remove(userId); }

    public boolean isSubscribed(Long userId) {
        return subscribers.contains(userId);
    }
}

