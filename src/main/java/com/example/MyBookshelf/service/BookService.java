package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.status.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserBookStatusRepository statusRepository;

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Page<BookEntity>> getAllBooks(Pageable pageable) {
        Page<BookEntity> page = bookRepository.findAllAsync(pageable);
        return CompletableFuture.completedFuture(page);
    }

    @Async("taskExecutor")
    public CompletableFuture<Optional<BookEntity>> getBookById(Long id) {
        return CompletableFuture.completedFuture(bookRepository.findByIdAsync(id));
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookEntity>> getBooksByGenre(String genre, Pageable pageable) {
        Page<BookEntity> page = bookRepository.findAll(pageable);

        List<BookEntity> filtered = page.getContent().stream()
                .filter(b -> genre.equalsIgnoreCase(b.getGenre()))
                .toList();

        Page<BookEntity> result = new PageImpl<>(filtered, pageable, filtered.size());

        return CompletableFuture.completedFuture(result);
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookEntity>> getBooksByStatusForUser(
            UserEntity user,
            String statusStr,
            Pageable pageable
    ) {
        ReadingStatus status = ReadingStatus.valueOf(statusStr.toUpperCase());

        List<UserBookStatusEntity> statuses =
                statusRepository.findByUserAndStatus(user, status);

        List<Long> bookIds = statuses.stream()
                .map(ubs -> ubs.getBook().getId())
                .toList();

        if (bookIds.isEmpty()) {
            Page<BookEntity> empty = new PageImpl<>(List.of(), pageable, 0);
            return CompletableFuture.completedFuture(empty);
        }

        List<BookEntity> allBooks = bookRepository.findAllWithReviewsByIdIn(bookIds);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allBooks.size());
        List<BookEntity> pageContent = start > end ? List.of() : allBooks.subList(start, end);

        Page<BookEntity> page = new PageImpl<>(pageContent, pageable, allBooks.size());

        return CompletableFuture.completedFuture(page);
    }


    @Async("taskExecutor")
    public CompletableFuture<Page<BookEntity>> findTopReviewed(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("reviewCount").descending());
        return CompletableFuture.completedFuture(bookRepository.findAllBy(page));
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookEntity>> findLeastReviewed(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("reviewCount").ascending());
        return CompletableFuture.completedFuture(bookRepository.findAllBy(page));
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookEntity>> findTopRated(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("rating").descending());
        return CompletableFuture.completedFuture(bookRepository.findAllBy(page));
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookEntity>> findLeastRated(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("rating").ascending());
        return CompletableFuture.completedFuture(bookRepository.findAllBy(page));
    }

    public BookEntity saveBook(BookEntity bookEntity) {
        return bookRepository.save(bookEntity);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}

