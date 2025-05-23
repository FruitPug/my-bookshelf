package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.*;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.status.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBookStatusService {
    private final UserBookStatusRepository repo;

    @Transactional
    public UserBookStatusEntity setStatus(UserEntity user, BookEntity book, ReadingStatus status) {
        UserBookStatusEntity ubs = repo.findByUserAndBook(user, book)
                .orElseGet(() -> UserBookStatusEntity.builder()
                        .user(user)
                        .book(book)
                        .build());
        ubs.setStatus(status);
        return repo.save(ubs);
    }

    public List<UserBookStatusEntity> getStatusesForUser(UserEntity user) {
        return repo.findByUser(user);
    }
}
