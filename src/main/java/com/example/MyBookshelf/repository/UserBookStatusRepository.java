package com.example.MyBookshelf.repository;

import com.example.MyBookshelf.status.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookStatusRepository extends JpaRepository<UserBookStatusEntity, Long> {

    Optional<UserBookStatusEntity> findByUserAndBook(UserEntity user, BookEntity book);

    List<UserBookStatusEntity> findByUser(UserEntity user);

    List<UserBookStatusEntity> findByUserAndStatus(UserEntity user, ReadingStatus status);
}
