package com.example.MyBookshelf.listener;

import com.example.MyBookshelf.event.BookFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookFinishedListener {

    // synchronous listener
    @EventListener
    public void handle(BookFinishedEvent evt) {
        // e.g. log or update stats
        log.info("User {} finished book {}",
                evt.getUser().getEmail(),
                evt.getBook().getTitle());
    }

    // async version (requires @EnableAsync)
    @Async("taskExecutor")
    @EventListener
    public void sendCongratulationsEmail(BookFinishedEvent evt) {
        // pretend to send an email
        log.info("Sending congrats email to {}", evt.getUser().getEmail());
    }
}
