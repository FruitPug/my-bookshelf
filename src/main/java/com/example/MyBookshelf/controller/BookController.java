package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.BookStatusDto;
import com.example.MyBookshelf.dto.UserBookStatusDto;
import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.mapper.UserBookStatusMapper;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.service.UserBookStatusService;
import com.example.MyBookshelf.status.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.BookMapper;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.repository.UserRepository;
import com.example.MyBookshelf.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserRepository userRepository;
    private final UserBookStatusRepository statusRepository;
    private final UserBookStatusService statusService;
    private final BookRepository bookRepository;
    private final Executor taskExecutor;

    @GetMapping
    public CompletableFuture<Page<BookResponseDto>> getAllBooksAsync(
            Pageable pageable,
            Authentication auth
    ) {
        UserEntity user = loadCurrentUser(auth);

        return bookService.getAllBooks(pageable)
                .thenApplyAsync(page ->
                        page.map(book -> {
                            ReadingStatus status = statusRepository
                                    .findByUserAndBook(user, book)
                                    .map(UserBookStatusEntity::getStatus)
                                    .orElse(null);
                            return BookMapper.toResponseDto(book, status);
                        }), taskExecutor);
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<BookResponseDto>> getBookByIdAsync(
            @PathVariable Long id,
            Authentication auth
    ) {
        // load the user now, outside the future
        UserEntity user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return bookService.getBookById(id)
                .thenApplyAsync(optBook -> optBook
                                .map(book -> {
                                    ReadingStatus status = statusRepository
                                            .findByUserAndBook(user, book)
                                            .map(UserBookStatusEntity::getStatus)
                                            .orElse(null);
                                    return BookMapper.toResponseDto(book, status);
                                })
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build()),
                        taskExecutor
                );
    }

    @GetMapping("/top-reviewed")
    public CompletableFuture<Page<BookResponseDto>> topReviewedAsync(
            @RequestParam(defaultValue = "5") int n
            ) {
        return bookService.findTopReviewed(n)
                .thenApplyAsync(bookPage ->
                                bookPage.map(book ->
                                        BookMapper.toResponseDto(book, null)
                                ),
                        taskExecutor
                );
    }

    @GetMapping("/least-reviewed")
    public CompletableFuture<Page<BookResponseDto>> leastReviewedAsync(
            @RequestParam(defaultValue = "5") int n
            ) {
        return bookService.findLeastReviewed(n)
                .thenApplyAsync(bookPage ->
                                bookPage.map(book ->
                                        BookMapper.toResponseDto(book, null)
                                ),
                        taskExecutor
                );
    }

    @GetMapping("/top-rated")
    public CompletableFuture<Page<BookResponseDto>> topRatedAsync(
            @RequestParam(defaultValue = "5") int n
            ) {
        return bookService.findTopRated(n)
                .thenApplyAsync(bookPage ->
                                bookPage.map(book ->
                                        BookMapper.toResponseDto(book, null)
                                ),
                        taskExecutor
                );
    }

    @GetMapping("/least-rated")
    public CompletableFuture<Page<BookResponseDto>> leastRatedAsync(
            @RequestParam(defaultValue = "5") int n
            ) {
        return bookService.findLeastRated(n)
                .thenApplyAsync(bookPage ->
                                bookPage.map(book ->
                                        BookMapper.toResponseDto(book, null)
                                ),
                        taskExecutor
                );
    }

    @GetMapping("/filter/genre/{genre}")
    public CompletableFuture<Page<BookResponseDto>> getByGenreAsync(
            @PathVariable String genre,
            Pageable pageable,
            Authentication auth
    ) {
        UserEntity user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return bookService.getBooksByGenre(genre, pageable)  // CF<Page<BookEntity>>
                .thenApplyAsync(bookPage ->
                                bookPage.map(book -> {
                                    ReadingStatus status = statusRepository
                                            .findByUserAndBook(user, book)
                                            .map(UserBookStatusEntity::getStatus)
                                            .orElse(null);
                                    return BookMapper.toResponseDto(book, status);
                                }),
                        taskExecutor
                );
    }

    @GetMapping("/filter/status/{status}")
    public CompletableFuture<Page<BookResponseDto>> getByStatusAsync(
            @PathVariable String status,
            Pageable pageable,
            Authentication auth
    ) {
        UserEntity user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return bookService.getBooksByStatusForUser(user, status, pageable)  // CF<List<BookEntity>>
                .thenApplyAsync(bookPage ->
                                bookPage.map(book -> {
                                    ReadingStatus bookStatus = statusRepository
                                            .findByUserAndBook(user, book)
                                            .map(UserBookStatusEntity::getStatus)
                                            .orElse(null);
                                    return BookMapper.toResponseDto(book, bookStatus);
                                }),
                        taskExecutor
                );
    }

    @GetMapping("/status")
    public ResponseEntity<Page<UserBookStatusDto>> listStatuses(
            Authentication auth,
            Pageable pageable
    ) {
        UserEntity user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Page<UserBookStatusEntity> page = statusService.getStatusesForUser(user, pageable);

        Page<UserBookStatusDto> dtoPage = page.map(UserBookStatusMapper::toDto);

        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@RequestBody BookCreateDto dto) {
        BookEntity saved = bookService.saveBook(BookMapper.fromCreateDto(dto));
        // no userStatus on creation
        return ResponseEntity.ok(BookMapper.toResponseDto(saved, null));
    }

    @PostMapping("/status/{bookId}")
    public ResponseEntity<UserBookStatusDto> setStatus(
            @PathVariable Long bookId,
            @RequestBody BookStatusDto statusDto,
            Authentication auth
    ) {
        UserEntity user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        UserBookStatusEntity ubs = statusService.setStatus(user, book, statusDto.status());
        return ResponseEntity.ok(UserBookStatusMapper.toDto(ubs));
    }

    /** Delete a book (admin use) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    private UserEntity loadCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"
                ));
    }
}
