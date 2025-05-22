package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.Book;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.util.IteratorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> getBooksByGenre(String genre) {
        List<Book> all = bookRepository.findAll();
        Iterator<Book> it = IteratorUtils.filterIterator(all, b -> genre.equalsIgnoreCase(b.getGenre()));
        List<Book> filtered = new ArrayList<>();
        while (it.hasNext()) {
            filtered.add(it.next());
        }
        return filtered;
    }

    public List<Book> getBooksByStatus(String status) {
        List<Book> all = bookRepository.findAll();
        Iterator<Book> it = IteratorUtils.filterIterator(all, b -> status.equalsIgnoreCase(b.getStatus()));
        List<Book> filtered = new ArrayList<>();
        while (it.hasNext()) {
            filtered.add(it.next());
        }
        return filtered;
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}

