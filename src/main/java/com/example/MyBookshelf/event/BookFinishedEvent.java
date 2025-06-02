package com.example.MyBookshelf.event;

import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookFinishedEvent extends ApplicationEvent {
    private final UserEntity userEntity;
    private final BookEntity bookEntity;

    public BookFinishedEvent(Object source, UserEntity userEntity, BookEntity bookEntity) {
        super(source);
        this.userEntity = userEntity;
        this.bookEntity = bookEntity;
    }
}
