package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.*;
import com.example.MyBookshelf.event.BookFinishedEvent;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.enums.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBookStatusService {
    private final UserBookStatusRepository statusRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public UserBookStatusEntity setStatus(UserEntity user, BookEntity book, ReadingStatus status) {
        UserBookStatusEntity ubs = statusRepository.findByUserAndBook(user, book)
                .orElseGet(() -> UserBookStatusEntity.builder().user(user).book(book).build());

        ubs.setStatus(status);
        UserBookStatusEntity saved = statusRepository.save(ubs);

        if (status == ReadingStatus.READ) {
            publisher.publishEvent(new BookFinishedEvent(this, user, book));
        }
        return saved;
    }

    public Page<UserBookStatusEntity> getStatusesForUser(UserEntity user, Pageable pageable) {
        return statusRepository.findByUser(user, pageable);
    }

    public Optional<UserBookStatusEntity> findByUserAndBook(UserEntity user, BookEntity book){
        return statusRepository.findByUserAndBook(user, book);
    }
}
