package com.epam.library.repository;

import com.epam.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

//Crud repository for the work with ´books´ table
public interface BookRepository extends PagingAndSortingRepository<Book, Integer> {
    Optional<Book> findByTitleAndAuthorAndPublicationYear(String name, String author, int publicationYear);

    Optional<Book> findById(Integer id);

    Optional<Book> save(Book book);

    Page<Book> findByTitleContaining(String title, Pageable pageable);

    Page<Book> findByAuthorContaining(String author, Pageable pageable);

    void delete(Book book);

    void deleteByTitle(String title);

    void deleteAll();
}
