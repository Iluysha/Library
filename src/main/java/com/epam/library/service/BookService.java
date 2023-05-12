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
    private final int PAGE_SIZE = 5;

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    //Add a copy of the book
    public void add(String bookTitle, String bookAuthor, String publicationYear) {
        log.info("Adding a book. Title: {}, Author: {}, PublicationYear: {}",
                bookTitle, bookAuthor, publicationYear);

        Optional<Book> optionalBook = repo.findByTitleAndAuthorAndPublicationYear(bookTitle,
                bookAuthor, Integer.parseInt(publicationYear));

        if(optionalBook.isPresent()) {
            log.info("Adding a copy of the book: {}", bookTitle);

            Book book = optionalBook.get();
            book.addCopy();
            repo.save(book);
        } else {
            log.info("Adding a new book: {}", bookTitle);

            Book newBook = new Book();
            newBook.setTitle(bookTitle);
            newBook.setAuthor(bookAuthor);
            newBook.setPublicationYear(Integer.parseInt(publicationYear));
            newBook.setNumOfCopies(1);
            newBook.setAvailableCopies(1);

            repo.save(newBook);
        }
    }

    //Update existing book
    public void save(Book book) {
        log.info("Saving book: {}", book);

        repo.save(book);
    }

    //Get list of all books
    public Page<Book> getBooks(String query, String field, int pageNo, String sortField, String sortOrder) {
        log.info("Searching books. Query: {}, Field: {}, Page: {}, SortField: {}, SortOrder: {}",
                query, field, pageNo, sortField, sortOrder);

        Sort sort = Sort.by(sortField.equals("author") ? "author" : "title");

        if (sortOrder.equals("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, sort);

        if (query != null && field != null && !query.equals("")) {
            return Objects.equals(field, "title") ? repo.findByTitle(query, pageable) : repo.findByAuthor(query, pageable);
        } else {
            return repo.findAll(pageable);
        }
    }

    public Optional<Book> getById(Integer id) {
        log.info("Getting book by ID: {}", id);
        return repo.findById(id);
    }
}
