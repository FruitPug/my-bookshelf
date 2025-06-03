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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserBookStatusService statusService;
    private final CurrentUserService currentUserService;

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Page<BookResponseDto>> getAllBooksDto(Pageable pageable) {
        Page<BookEntity> entityPage = bookRepository.findAllAsync(pageable);

        return getPageCompletableFuture(entityPage);
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<ResponseEntity<BookResponseDto>> getBookByIdDto(Long id) {
        BookEntity book = bookRepository.findByIdAsync(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        UserEntity user = currentUserService.get();

        ReadingStatus status = statusService
                .findByUserAndBook(user, book)
                .map(UserBookStatusEntity::getStatus)
                .orElse(null);

        BookResponseDto dto = BookMapper.toResponseDto(book, status);

        return CompletableFuture.completedFuture(ResponseEntity.ok(dto));
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Page<BookResponseDto>> getBooksByGenreDto(
            String genre,
            Pageable pageable
    ) {
        Page<BookEntity> page = bookRepository.findAll(pageable);

        List<BookEntity> filtered = page.getContent().stream()
                .filter(b -> genre.equalsIgnoreCase(b.getGenre()))
                .toList();

        Page<BookEntity> entityPage = new PageImpl<>(filtered, pageable, filtered.size());

        return getPageCompletableFuture(entityPage);
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<BookResponseDto>> getBooksByGenreWithAsyncRatingFilter(
            String genre
    ) {
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

    @Async("taskExecutor")
    public CompletableFuture<Page<BookResponseDto>> findTopReviewedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("reviewCount").descending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        Page<BookResponseDto> dtoPage = entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );

        return CompletableFuture.completedFuture(dtoPage);
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookResponseDto>> findLeastReviewedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("reviewCount").ascending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        Page<BookResponseDto> dtoPage = entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );

        return CompletableFuture.completedFuture(dtoPage);
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookResponseDto>> findTopRatedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("rating").descending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        Page<BookResponseDto> dtoPage = entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );

        return CompletableFuture.completedFuture(dtoPage);
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BookResponseDto>> findLeastRatedDto(int n) {
        PageRequest page = PageRequest.of(0, n, Sort.by("rating").ascending());
        Page<BookEntity> entityPage = bookRepository.findAllBy(page);

        Page<BookResponseDto> dtoPage = entityPage.map(book ->
                BookMapper.toResponseDto(book, null)
        );

        return CompletableFuture.completedFuture(dtoPage);
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
        return bookRepository.findByIdAsync(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    public ResponseEntity<Void> deleteBook(Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @NotNull
    private CompletableFuture<Page<BookResponseDto>> getPageCompletableFuture(Page<BookEntity> entityPage) {
        UserEntity user = currentUserService.get();

        Page<BookResponseDto> dtoPage = entityPage.map(book -> {
            ReadingStatus status = statusService
                    .findByUserAndBook(user, book)
                    .map(UserBookStatusEntity::getStatus)
                    .orElse(null);

            return BookMapper.toResponseDto(book, status);
        });

        return CompletableFuture.completedFuture(dtoPage);
    }
}

