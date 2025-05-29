package com.example.MyBookshelf.listener;

import com.example.MyBookshelf.event.BookFinishedEvent;
import com.example.MyBookshelf.notification.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookFinishedNotificationListener {
    private final SubscriptionService subs;

    @EventListener
    public void onBookFinished(BookFinishedEvent ev) {
        Long uid = ev.getUser().getId();
        if (!subs.isSubscribed(uid)) return;  // skip if user unsubscribed

        // simulate sending an email / push
        log.info("✉️ Notifying {} that they finished “{}”",
                ev.getUser().getEmail(), ev.getBook().getTitle());
    }
}

