package com.epam.library.repository;

import com.epam.library.entity.Book;
import org.springframework.data.repository.CrudRepository;

//Crud repository for the work with ´books´ table
public interface BookRepository extends CrudRepository<Book, Integer> {}
