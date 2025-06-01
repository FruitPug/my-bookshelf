package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.ReviewEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.ReviewRepository;
import com.example.MyBookshelf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;


    public List<ReviewEntity> getReviewsByUserEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return reviewRepository.findByUser(user);
    }

    @Transactional
    @CacheEvict(value = "recommendations", key = "#user.id")
    public ReviewEntity addReview(Long bookId, ReviewEntity reviewEntity, UserEntity user) {
        Optional<BookEntity> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new EntityNotFoundException("Book not found: " + bookId);
        }
        reviewEntity.setBook(optionalBook.get());

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        reviewRepository.findByUserAndBook(user, book)
                .ifPresent(r -> {
                    throw new IllegalStateException("You have already reviewed this book");
                });

        ReviewEntity saved = reviewRepository.save(reviewEntity);

        int oldCount = book.getReviewCount();
        double oldAvg  = book.getRating();

        int newCount = oldCount + 1;
        double newAvg = ((oldAvg * oldCount) + saved.getRating()) / newCount;

        book.setReviewCount(newCount);
        book.setRating(newAvg);

        bookRepository.save(book);

        return saved;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        Long bookId = review.getBook().getId();

        reviewRepository.deleteById(reviewId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        List<ReviewEntity> remaining = reviewRepository.findByBookId(bookId).stream().toList();
        int count = remaining.size();
        double avg   = remaining.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        book.setReviewCount(count);
        book.setRating(avg);

        bookRepository.save(book);
    }
}
