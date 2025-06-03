package com.example.MyBookshelf.service;

import com.example.MyBookshelf.dto.UserBookStatusDto;
import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.BookMapper;
import com.example.MyBookshelf.mapper.UserBookStatusMapper;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.enums.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserBookStatusService statusService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<BookResponseDto> getAllBooksDto(Pageable pageable) {
        Page<BookEntity> entityPage = bookRepository.findAll(pageable);

        return getPage(entityPage);
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDto> getAllBooksStream(Pageable pageable) {
        UserEntity user = currentUserService.get();
        long offset = pageable.getOffset();
        int size = pageable.getPageSize();

        try (Stream<BookEntity> stream = bookRepository.streamAll()) {

            List<BookResponseDto> content = stream
                    .skip(offset)
                    .limit(size)
                    .map(book -> {
                        ReadingStatus status = statusService
                                .findByUserAndBook(user, book)
                                .map(UserBookStatusEntity::getStatus)
                                .orElse(null);

                        return BookMapper.toResponseDto(book, status);
                    })
                    .toList();

            long total = bookRepository.count();

            return new PageImpl<>(content, pageable, total);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<BookResponseDto> getBookByIdDto(Long id) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        UserEntity user = currentUserService.get();

        ReadingStatus status = statusService
                .findByUserAndBook(user, book)
                .map(UserBookStatusEntity::getStatus)
                .orElse(null);

        BookResponseDto dto = BookMapper.toResponseDto(book, status);

        return ResponseEntity.ok(dto);
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDto> getBooksByGenreDto(
            String genre,
            Pageable pageable
    ) {
        Page<BookEntity> page = bookRepository.findAll(pageable);

        List<BookEntity> filtered = page.getContent().stream()
                .filter(b -> genre.equalsIgnoreCase(b.getGenre()))
                .toList();

        Page<BookEntity> entityPage = new PageImpl<>(filtered, pageable, filtered.size());

        return getPage(entityPage);
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<BookResponseDto>> getBooksByGenreWithAsyncRatingFilter(
            String genre,
            boolean cancel
    ) {
        if (cancel) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        UserEntity user = currentUserService.get();
        Page<BookEntity> page = bookRepository.findAll(Pageable.unpaged());

        List<BookEntity> filteredByGenre = page.getContent().stream()
                .filter(b -> genre.equalsIgnoreCase(b.getGenre()))
                .toList();

        List<CompletableFuture<Optional<BookEntity>>> futures = filteredByGenre.stream()
                .map(book -> CompletableFuture.supplyAsync(() ->
                        (book.getRating() > 4)
                        ? Optional.of(book)
                        : Optional.<BookEntity>empty())).toList();

        filteredByGenre.forEach(book -> Hibernate.initialize(book.getReviewEntities()));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(book -> {
                            ReadingStatus status = statusService
                                    .findByUserAndBook(user, book)
                                    .map(UserBookStatusEntity::getStatus)
                                    .orElse(null);
                            return BookMapper.toResponseDto(book, status);
                        })
                        .toList());
    }

    public Page<BookResponseDto> getBooksByStatusDto(
            String statusStr,
            Pageable pageable
    ) {
        UserEntity user = currentUserService.get();

        ReadingStatus targetStatus = ReadingStatus.valueOf(statusStr.trim().toUpperCase());

        Page<UserBookStatusEntity> statusPage =
                statusService.findByUserAndStatus(user, targetStatus, pageable);

        return statusPage.map(ubs -> {
            BookEntity book = ubs.getBook();
            ReadingStatus actualStatus = ubs.getStatus();
            return BookMapper.toResponseDto(book, actualStatus);
        });
    }

    public Page<BookResponseDto> findTopReviewedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("reviewCount").descending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        return entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );
    }

    public Page<BookResponseDto> findLeastReviewedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("reviewCount").ascending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        return entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );
    }

    public Page<BookResponseDto> findTopRatedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("rating").descending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        return entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );
    }

    public Page<BookResponseDto> findLeastRatedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("rating").ascending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        return entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );
    }

    public ResponseEntity<Page<UserBookStatusDto>> listStatuses(
            Pageable pageable
    ) {
        UserEntity user = currentUserService.get();

        Page<UserBookStatusEntity> page = statusService.getStatusesForUser(user, pageable);

        Page<UserBookStatusDto> dtoPage = page.map(UserBookStatusMapper::toDto);

        return ResponseEntity.ok(dtoPage);
    }

    public ResponseEntity<BookResponseDto> addBook(BookCreateDto dto) {
        BookEntity bookEntity = BookMapper.fromCreateDto(dto);
        bookRepository.save(bookEntity);
        return ResponseEntity.ok(BookMapper.toResponseDto(bookEntity, null));
    }

    public BookEntity findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    public ResponseEntity<Void> deleteBook(Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @NotNull
    private Page<BookResponseDto> getPage(Page<BookEntity> entityPage) {
        UserEntity user = currentUserService.get();

        return entityPage.map(book -> {
            ReadingStatus status = statusService
                    .findByUserAndBook(user, book)
                    .map(UserBookStatusEntity::getStatus)
                    .orElse(null);

            return BookMapper.toResponseDto(book, status);
        });
    }
}

