package com.example.MyBookshelf.repository;

import com.example.MyBookshelf.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}