package com.example.MyBookshelf.repository;

import com.example.MyBookshelf.entity.BookEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    @EntityGraph(
            value = "Book.withReviewsAndUsers",
            type  = EntityGraph.EntityGraphType.LOAD
    )
    @Query("SELECT b FROM BookEntity b")
    Page<BookEntity> findAllAsync(Pageable pageable);


    @EntityGraph(
            value = "Book.withReviewsAndUsers",
            type  = EntityGraph.EntityGraphType.LOAD
    )
    @Query("SELECT b FROM BookEntity b WHERE b.id = :id")
    Optional<BookEntity> findByIdAsync(@Param("id") Long id);


    @EntityGraph(value = "Book.withReviewsAndUsers", type = EntityGraph.EntityGraphType.LOAD)
    Page<BookEntity> findAllBy(Pageable pageable);
}

