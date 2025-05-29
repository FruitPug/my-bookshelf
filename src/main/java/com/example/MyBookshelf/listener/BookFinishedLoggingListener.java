package com.example.MyBookshelf.listener;

import com.example.MyBookshelf.event.BookFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookFinishedLoggingListener {

    @EventListener
    public void onBookFinished(BookFinishedEvent ev) {
        log.info("🎉 User {} finished book {}",
                ev.getUser().getEmail(), ev.getBook().getTitle());
    }
}
