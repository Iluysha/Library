package com.epam.library.service;

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

    private static final Logger log = LogManager.getLogger(BookService.class);

    private final int pageSize = 5;

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    //Update existing book
    public Book save(Book book) {
        log.info("Saving book: {}", book);
        repo.save(book);
        return book;
    }

    //Add a copy of the book
    public Book add(String bookTitle, String bookAuthor, String publicationYear) {
        log.info("Adding a book. Title: {}, Author: {}, PublicationYear: {}",
                bookTitle, bookAuthor, publicationYear);

        Optional<Book> optionalBook = repo.findByTitleAndAuthorAndPublicationYear(bookTitle,
                bookAuthor, Integer.parseInt(publicationYear));

        if(optionalBook.isPresent()) {
            log.info("Adding a copy of the book: {}", bookTitle);

            Book book = optionalBook.get();
            book.addCopy();
            repo.save(book);

            return book;
        } else {
            try {
                log.info("Adding a new book: {}", bookTitle);

                Book newBook = new Book(bookTitle, bookAuthor, Integer.parseInt(publicationYear));
                repo.save(newBook);

                return newBook;
            } catch (NumberFormatException e) {
                log.error("Wrong input: {}, {}, {}", bookTitle, bookAuthor, publicationYear);
                return null;
            }
        }
    }

    //Get list of all books
    public Page<Book> getBooks(String searchQuery, String searchField, int pageNo, String sortField, String sortOrder) {
        log.info("Searching books. Query: {}, Field: {}, Page: {}, SortField: {}, SortOrder: {}",
                searchQuery, searchField, pageNo, sortField, sortOrder);

        Sort sort = Sort.by(sortField.equals("author") ? "author" : "title");
        sort = sortOrder.equals("desc") ? sort.descending() : sort.ascending();

        if (pageNo < 1) {
            log.warn("Invalid page number: {}", pageNo);
            return null;
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        Page<Book> page;

        if (!searchField.equals("") && !searchQuery.equals("")) {
            if(Objects.equals(searchField, "author")) {
                log.info("Searching books by author");
                page = repo.findByAuthor(searchQuery, pageable);
            } else {
                log.info("Searching books by title");
                page = repo.findByTitle(searchQuery, pageable);
            }
        } else {
            log.info("Getting all books");
            page = repo.findAll(pageable);
        }

        if (page.getTotalPages() != 0 && pageNo > page.getTotalPages()) {
            log.error("Invalid page number: {}", pageNo);
            return null;
        } else {
            return page;
        }
    }

    public Optional<Book> findById(Integer id) {
        log.info("Getting book by ID: {}", id);
        return repo.findById(id);
    }

    public int getPageSize() {
        return pageSize;
    }
}
