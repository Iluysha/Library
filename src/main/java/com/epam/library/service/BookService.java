package com.epam.library.service;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.springframework.transaction.annotation.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Service class responsible for handling book-related operations.
 */
@Service
public class BookService {

    private static final Logger log = LogManager.getLogger(BookService.class);

    private final int pageSize = 5;

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    /**
     * Saves the book to the repository.
     *
     * @param book the book to be saved
     * @return the saved book
     */
    public Book save(Book book) {
        log.info("Saving book: {}", book);
        repo.save(book);
        return book;
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param bookId the ID of the book
     * @return the book if found, or throws exception otherwise.
     */
    public Book findById(Integer bookId) throws Exception {
        log.info("Getting book by ID: {}", bookId);
        return repo.findById(bookId).orElseThrow(() -> new Exception("Book " + bookId + " not found"));
    }

    /**
     * Adds a copy of the book or adds a new book if no copy exists.
     *
     * @param bookTitle        the title of the book
     * @param bookAuthor       the author of the book
     * @param publicationYear  the publication year of the book
     * @return the added book or throws exception if there was an error
     */
    @Transactional
    public Book add(String bookTitle, String bookAuthor, String publicationYear) throws Exception {
        log.info("Adding a book. Title: {}, Author: {}, PublicationYear: {}",
                bookTitle, bookAuthor, publicationYear);

        try {
            Optional<Book> optionalBook = repo.findByTitleAndAuthorAndPublicationYear(bookTitle,
                    bookAuthor, Integer.parseInt(publicationYear));

            // Check if the book already exists
            if(optionalBook.isPresent()) {
                log.info("Adding a copy of the book: {}", bookTitle);

                Book book = optionalBook.get();
                book.addCopy();
                repo.save(book);

                return book;
            } else {
                log.info("Adding a new book: {}", bookTitle);

                Book newBook = new Book(bookTitle, bookAuthor, Integer.parseInt(publicationYear));
                repo.save(newBook);

                return newBook;
            }
        } catch (NumberFormatException e) {
            log.error("Wrong input: {}, {}, {}", bookTitle, bookAuthor, publicationYear);
            throw new Exception("Wrong input");
        }
    }

    /**
     * Retrieves a paginated list of books based on the search criteria.
     *
     * @param searchQuery  the search query
     * @param searchField  the field to search on (title or author)
     * @param pageNo       the page number
     * @param sortField    the field to sort on (title or author)
     * @param sortOrder    the sort order (asc or desc)
     * @return the paginated list of books or throws exception if there was an error
     */
    public Page<Book> getBooks(String searchQuery, String searchField, int pageNo, String sortField, String sortOrder) {
        log.info("Searching books. Query: {}, Field: {}, Page: {}, SortField: {}, SortOrder: {}",
                searchQuery, searchField, pageNo, sortField, sortOrder);

        Sort sort = Sort.by(sortField.equals("author") ? "author" : "title");
        sort = sortOrder.equals("desc") ? sort.descending() : sort.ascending();

        if (pageNo < 1) {
            log.warn("Invalid page number: {}", pageNo);
            throw new IllegalArgumentException("Invalid page number: " + pageNo);
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Book> page;

        // If search fields are empty, get all books
        if (!searchField.equals("") && !searchQuery.equals("")) {
            if(Objects.equals(searchField, "author")) {
                log.info("Searching books by author");
                page = repo.findByAuthorContaining(searchQuery, pageable);
            } else {
                log.info("Searching books by title");
                page = repo.findByTitleContaining(searchQuery, pageable);
            }
        } else {
            log.info("Getting all books");
            page = repo.findAll(pageable);
        }

        if (page.getTotalPages() != 0 && pageNo > page.getTotalPages()) {
            log.error("Invalid page number: {}", pageNo);
            throw new IllegalArgumentException("Invalid page number: " + pageNo);
        } else {
            // If pages number is zero, return it
            return page;
        }
    }

    /**
     * Retrieves the page size used for pagination.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }
}
