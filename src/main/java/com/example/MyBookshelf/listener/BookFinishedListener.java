package com.example.MyBookshelf.listener;

import com.example.MyBookshelf.event.BookFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookFinishedListener {

    @EventListener
    public void handle(BookFinishedEvent evt) {
        // e.g. log or update stats
        log.info("User {} finished book {}",
                evt.getUserEntity().getEmail(),
                evt.getBookEntity().getTitle());
    }

    @Async("taskExecutor")
    @EventListener
    public void sendCongratulationsEmail(BookFinishedEvent evt) {
        // pretend to send an email
        log.info("Sending congrats email to {}", evt.getUserEntity().getEmail());
    }
}
