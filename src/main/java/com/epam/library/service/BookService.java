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

        int publicationYearInt;

        try {
            publicationYearInt = Integer.parseInt(publicationYear);
        } catch (NumberFormatException e) {
            log.error("Wrong year input: {}", publicationYear);
            throw new Exception("Wrong input");
        }

        Optional<Book> optionalBook = repo.findByTitleAndAuthorAndPublicationYear(bookTitle,
                bookAuthor, publicationYearInt);

        // Check if the book already exists
        if(optionalBook.isPresent()) {
            log.info("Adding a copy of the book: {}", bookTitle);

            Book book = optionalBook.get();
            book.addCopy();
            repo.save(book);

            return book;
        }

        log.info("Adding a new book: {}", bookTitle);

        Book newBook = new Book(bookTitle, bookAuthor, publicationYearInt);
        repo.save(newBook);

        return newBook;
    }

    @Transactional
    public Book edit(Integer bookId, String bookTitle, String bookAuthor, String publicationYear) throws Exception {
        log.info("Editing a book. Title: {}, Author: {}, PublicationYear: {}",
                bookTitle, bookAuthor, publicationYear);

        int publicationYearInt;

        try {
            publicationYearInt = Integer.parseInt(publicationYear);
        } catch (NumberFormatException e) {
            log.error("Wrong year input: {}", publicationYear);
            throw new Exception("Wrong input");
        }

        if(repo.findByTitleAndAuthorAndPublicationYear(bookTitle,
                bookAuthor, publicationYearInt).isPresent()) {
            log.error("Book already exists: {}, {}, {}", bookTitle, bookAuthor, publicationYear);
            throw new Exception("Book already exists");
        }

        Book book;

        try {
            book = findById(bookId);
        } catch (Exception e) {
            log.error("Book not found with id: {}", bookId);
            throw new Exception("Wrong input");
        }

        book.setTitle(bookTitle);
        book.setAuthor(bookAuthor);
        book.setPublicationYear(publicationYearInt);

        repo.save(book);
        log.info("The book is saved");
        return book;
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
        if (!searchField.isBlank() && !searchQuery.isBlank()) {
            if(searchField.equals("author")) {
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
        }

        return page;
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
