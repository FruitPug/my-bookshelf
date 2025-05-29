package com.example.MyBookshelf.event;

import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookFinishedEvent extends ApplicationEvent {
    private final UserEntity user;
    private final BookEntity book;

    public BookFinishedEvent(Object source, UserEntity user, BookEntity book) {
        super(source);
        this.user = user;
        this.book = book;
    }
}
