package com.epam.library.service;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

//Service for crud operations with books
@Service
public class BookService {

    private final int PAGE_SIZE = 3;

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    //Add a copy of the book
    public void add(Book newBook) {
        Book book = repo.findByTitleAndAuthorAndPublicationYear(newBook.getTitle(),
                newBook.getAuthor(), newBook.getPublicationYear());

        if(book == null) {
            newBook.setNumOfCopies(1);
            newBook.setAvailableCopies(1);
            repo.save(newBook);
        } else {
            book.addCopy();
            repo.save(book);
        }
    }

    //Update existing book
    public void save(Book book) {
        repo.save(book);
    }

    //Check if book is in the database
    public boolean exist(Book book) {
        return getById(book.getId()).isPresent();
    }

    //Get list of all books
    public Page<Book> getBooks(int pageNo, String sortField, String sortOrder) {
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
        return repo.findById(id);
    }
}
