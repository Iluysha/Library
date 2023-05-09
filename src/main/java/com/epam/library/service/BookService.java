package com.epam.library.service;

import com.epam.library.controller.BookController;
import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

//Service for crud operations with books
@Service
public class BookService {

    private static final Logger log = LogManager.getLogger(BookController.class);
    private final int PAGE_SIZE = 5;

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    //Add a copy of the book
    public void add(Book newBook) {
        log.info("Adding a book: {}", newBook);

        Book book = repo.findByTitleAndAuthorAndPublicationYear(newBook.getTitle(),
                newBook.getAuthor(), newBook.getPublicationYear());

        if(book == null) {
            log.info("Adding a new book");

            newBook.setNumOfCopies(1);
            newBook.setAvailableCopies(1);
            repo.save(newBook);
        } else {
            log.info("Adding a copy of the book");

            book.addCopy();
            repo.save(book);
        }
    }

    //Update existing book
    public void save(Book book) {
        log.info("Saving book: {}", book);

        repo.save(book);
    }

    //Check if book is in the database
    public boolean exist(Book book) {
        log.info("Checking if book exists: {}", book);

        return getById(book.getId()).isPresent();
    }

    //Get list of all books
    public Page<Book> getBooks(int pageNo, String sortField, String sortOrder) {
        log.info("Getting books. Page: {}, SortField: {}, SortOrder: {}", pageNo, sortField, sortOrder);

        Sort sort = Sort.by(sortField);
        if (sortOrder.equals("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, sort);
        return repo.findAll(pageable);
    }

    public Page<Book> searchBooks(String query, String field, int pageNo, String sortField, String sortOrder) {
        log.info("Searching books. Query: {}, Field: {}, Page: {}, SortField: {}, SortOrder: {}", query, field, pageNo, sortField, sortOrder);

        Sort sort = Sort.by(sortField);
        if (sortOrder.equals("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, sort);

        if(Objects.equals(field, "title")) {
            return repo.findByTitle(query, pageable);
        } else {
            return repo.findByAuthor(query, pageable);
        }
    }

    //Get book by id, or empty Optional object there is no book with such id
    public Optional<Book> getById(Integer id) {
        log.info("Getting book by ID: {}", id);

        return repo.findById(id);
    }
}
