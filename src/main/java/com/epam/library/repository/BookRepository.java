package com.epam.library.repository;

import com.epam.library.entity.Book;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

//Crud repository for the work with ´books´ table
public interface BookRepository extends CrudRepository<Book, Integer> {
    Book findByNameAndAuthorAndPublicationYear(String name, String author, int publicationYear);
}
