package com.example.MyBookshelf.service;

import com.example.MyBookshelf.dto.UserBookStatusDto;
import com.example.MyBookshelf.entity.*;
import com.example.MyBookshelf.event.BookFinishedEvent;
import com.example.MyBookshelf.mapper.UserBookStatusMapper;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.enums.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBookStatusService {
    private final UserBookStatusRepository statusRepository;
    private final ApplicationEventPublisher publisher;
    private final CurrentUserService currentUserService;

    @Transactional
    public ResponseEntity<UserBookStatusDto> setStatus(BookEntity book, ReadingStatus status) {
        UserEntity user = currentUserService.get();
        UserBookStatusEntity ubs = statusRepository.findByUserAndBook(user, book)
                .orElseGet(() -> UserBookStatusEntity.builder().user(user).book(book).build());

        ubs.setStatus(status);
        statusRepository.save(ubs);

        if (status == ReadingStatus.READ) {
            publisher.publishEvent(new BookFinishedEvent(this, user, book));
        }

        return ResponseEntity.ok(UserBookStatusMapper.toDto(ubs));
    }

    public Page<UserBookStatusEntity> getStatusesForUser(UserEntity user, Pageable pageable) {
        return statusRepository.findByUser(user, pageable);
    }

    public Optional<UserBookStatusEntity> findByUserAndBook(UserEntity user, BookEntity book){
        return statusRepository.findByUserAndBook(user, book);
    }

    public Page<UserBookStatusEntity> findByUserAndStatus(
            UserEntity user,
            ReadingStatus status,
            Pageable pageable
    ){
        return statusRepository.findByUserAndStatus(user, status, pageable);
    }
}
