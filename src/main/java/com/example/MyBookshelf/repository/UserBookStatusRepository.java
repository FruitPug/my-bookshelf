package com.example.MyBookshelf.repository;

import com.example.MyBookshelf.enums.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookStatusRepository extends JpaRepository<UserBookStatusEntity, Long> {

    Optional<UserBookStatusEntity> findByUserAndBook(UserEntity user, BookEntity book);

    Page<UserBookStatusEntity> findByUser(UserEntity user, Pageable pageable);

    List<UserBookStatusEntity> findByUserAndStatus(UserEntity user, ReadingStatus status);

    void deleteByUser(UserEntity user);

}
