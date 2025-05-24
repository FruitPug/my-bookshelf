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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserRepository userRepository;
    private final UserBookStatusRepository statusRepository;
    private final UserBookStatusService statusService;
    private final BookRepository bookRepository;

    /** Paginated list, each with the calling user’s status */
    @GetMapping
    public Page<BookResponseDto> getAllBooks(Pageable pageable, Authentication auth) {
        UserEntity user = loadCurrentUser(auth);
        return bookService.getAllBooks(pageable)
                .map(book -> {
                    ReadingStatus status = statusRepository
                            .findByUserAndBook(user, book)
                            .map(UserBookStatusEntity::getStatus)
                            .orElse(null);
                    return BookMapper.toResponseDto(book, status);
                });
    }

    /** Single book by ID, with user’s status */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(
            @PathVariable Long id,
            Authentication auth
    ) {
        UserEntity user = loadCurrentUser(auth);

        return bookService.getBookById(id)
                .map(book -> {
                    ReadingStatus status = statusRepository
                            .findByUserAndBook(user, book)
                            .map(UserBookStatusEntity::getStatus)
                            .orElse(null);
                    return BookMapper.toResponseDto(book, status);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Filter by genre, with user’s status */
    @GetMapping("/filter/genre/{genre}")
    public List<BookResponseDto> getByGenre(
            @PathVariable String genre,
            Authentication auth
    ) {
        UserEntity user = loadCurrentUser(auth);

        return bookService.getBooksByGenre(genre).stream()
                .map(book -> {
                    ReadingStatus status = statusRepository
                            .findByUserAndBook(user, book)
                            .map(UserBookStatusEntity::getStatus)
                            .orElse(null);
                    return BookMapper.toResponseDto(book, status);
                })
                .toList();
    }

    /** Filter by status text (global; still using old iterator) */
    @GetMapping("/filter/status/{status}")
    public List<BookResponseDto> getByStatus(
            @PathVariable String status,
            Authentication auth
    ) {
        UserEntity user = loadCurrentUser(auth);

        List<BookEntity> books = bookService.getBooksByStatusForUser(user, status);

        return books.stream()
                .map(book -> {
                    return BookMapper.toResponseDto(book, ReadingStatus.valueOf(status.toUpperCase()));
                })
                .toList();
    }

    @GetMapping("/status")
    public ResponseEntity<List<UserBookStatusDto>> listStatuses(Authentication auth) {
        Optional<UserEntity> optUser = userRepository.findByEmail(auth.getName());
        if (optUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserEntity user = optUser.get();
        List<UserBookStatusEntity> list = statusService.getStatusesForUser(user);
        List<UserBookStatusDto> dtos = list.stream()
                .map(UserBookStatusMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /** Create a book (registration only) */
    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@RequestBody BookCreateDto dto) {
        BookEntity saved = bookService.saveBook(BookMapper.fromCreateDto(dto));
        // no userStatus on creation
        return ResponseEntity.ok(BookMapper.toResponseDto(saved, null));
    }

    @PostMapping("/{bookId}/status")
    public ResponseEntity<UserBookStatusDto> setStatus(
            @PathVariable Long bookId,
            @RequestBody BookStatusDto statusDto,
            Authentication auth
    ) {
        Optional<UserEntity> optUser = userRepository.findByEmail(auth.getName());
        if (optUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserEntity user = optUser.get();

        Optional<BookEntity> optBook = bookRepository.findById(bookId);
        if (optBook.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BookEntity book = optBook.get();

        UserBookStatusEntity ubs = statusService.setStatus(user, book, statusDto.status());
        return ResponseEntity.ok(UserBookStatusMapper.toDto(ubs));
    }

    /** Delete a book (admin use) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // --- helper to load current UserEntity or 401 ---
    private UserEntity loadCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"
                ));
    }
}
