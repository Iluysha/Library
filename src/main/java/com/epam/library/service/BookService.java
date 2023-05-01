package com.epam.library.service;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//Service for crud operations with books
@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    //Add a copy of the book
    public void add(Book newBook) {
        Book book = repo.findByNameAndAuthorAndPublicationYear(newBook.getName(),
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
    public List<Book> findAll() {
        return (List<Book>) repo.findAll();
    }

    //Get book by id, or empty Optional object there is no book with such id
    public Optional<Book> getById(Integer id) {
        return repo.findById(id);
    }
}
