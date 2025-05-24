package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.status.ReadingStatus;
import com.example.MyBookshelf.entity.UserBookStatusEntity;
import com.example.MyBookshelf.util.IteratorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserBookStatusRepository statusRepository;

    public Page<BookEntity> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<BookEntity> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<BookEntity> getBooksByGenre(String genre) {
        List<BookEntity> all = bookRepository.findAll();
        Iterator<BookEntity> it = IteratorUtils.filterIterator(all, b -> genre.equalsIgnoreCase(b.getGenre()));
        List<BookEntity> filtered = new ArrayList<>();
        while (it.hasNext()) {
            filtered.add(it.next());
        }
        return filtered;
    }

    public List<BookEntity> getBooksByStatusForUser(UserEntity user, String statusStr) {
        ReadingStatus status = ReadingStatus.valueOf(statusStr.toUpperCase());
        List<UserBookStatusEntity> statuses = statusRepository.findByUserAndStatus(user, status);
        return statuses.stream()
                .map(UserBookStatusEntity::getBook)
                .collect(Collectors.toList());
    }

    public BookEntity saveBook(BookEntity bookEntity) {
        return bookRepository.save(bookEntity);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}

