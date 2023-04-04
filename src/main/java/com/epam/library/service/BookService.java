package com.epam.library.service;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//Service for crud operations with books
@Service
public class BookService {

    @Autowired
    private BookRepository repo;

    //Add new book or update existing
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
