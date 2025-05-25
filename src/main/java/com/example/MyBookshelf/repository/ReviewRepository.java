package com.example.MyBookshelf.repository;

import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.ReviewEntity;
import com.example.MyBookshelf.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    Optional<ReviewEntity> findByUserAndBook(UserEntity user, BookEntity book);

    List<ReviewEntity> findByBookId(Long bookId);

    List<ReviewEntity> findByUser(UserEntity user);

}