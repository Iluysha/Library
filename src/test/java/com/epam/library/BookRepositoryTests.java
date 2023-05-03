package com.epam.library;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class BookRepositoryTests {
    @Autowired
    private BookRepository repo;

    @Test
    public void testAddNew() {
        Book book = new Book();
        book.setName("Three Comrades");
        book.setAuthor("Erich Maria Remarque");
        book.setNumOfCopies(3);
        book.setAvailableCopies(1);
        book.setPublicationYear(1938);

        Book savedBook = repo.save(book);

        Assertions.assertThat(savedBook).isNotNull();
        Assertions.assertThat(savedBook.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAll() {
        Iterable<Book> books = repo.findAll(Sort.by("name").ascending());
        Assertions.assertThat(books).hasSizeGreaterThan(0);

        for (Book book : books) {
            System.out.println(book);
        }
    }
}
