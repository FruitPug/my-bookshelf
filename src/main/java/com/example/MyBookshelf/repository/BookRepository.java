package com.example.MyBookshelf.repository;

import com.example.MyBookshelf.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.stream.Stream;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Page<BookEntity> findAllBy(Pageable pageable);

    @Query("SELECT b FROM BookEntity b")
    Stream<BookEntity> streamAll();
}

