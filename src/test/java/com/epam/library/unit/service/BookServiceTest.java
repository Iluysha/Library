package com.epam.library.unit.service;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import com.epam.library.service.BookService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {

    @Mock
    private BookRepository repo;

    @InjectMocks
    private BookService bookService;

    @Test
    public void testSave() {
        // Arrange
        Book book = new Book("The Hobbit", "J.R.R. Tolkien", 1937);
        book.setNumOfCopies(1);
        book.setAvailableCopies(1);

        Mockito.when(repo.save(book)).thenReturn(Optional.of(book));

        // Act
        Book actualBook = bookService.save(book);

        // Assert
        assertEquals(book, actualBook);
    }

    @Test
    public void testAddNewBook() {
        // Arrange
        String bookTitle = "The Lord of the Rings";
        String bookAuthor = "J.R.R. Tolkien";
        String publicationYear = "1954";

        Book expectedBook = new Book();
        expectedBook.setTitle(bookTitle);
        expectedBook.setAuthor(bookAuthor);
        expectedBook.setPublicationYear(Integer.parseInt(publicationYear));
        expectedBook.setNumOfCopies(1);
        expectedBook.setAvailableCopies(1);

        Mockito.when(repo.findByTitleAndAuthorAndPublicationYear(bookTitle, bookAuthor, Integer.parseInt(publicationYear)))
                .thenReturn(Optional.empty());
        Mockito.when(repo.save(Mockito.any(Book.class))).thenReturn(Optional.of(expectedBook));

        // Act
        Book actualBook = bookService.add(bookTitle, bookAuthor, publicationYear);

        // Assert
        assertEquals(expectedBook, actualBook);
    }

    @Test
    public void testAddExistingBook() {
        // Arrange
        String bookTitle = "Existing Book";
        String bookAuthor = "Test Author";
        String publicationYear = "2022";

        Book existingBook = new Book(bookTitle, bookAuthor, Integer.parseInt(publicationYear));
        existingBook.setNumOfCopies(1);
        existingBook.setAvailableCopies(1);

        Mockito.when(repo.findByTitleAndAuthorAndPublicationYear(bookTitle, bookAuthor, Integer.parseInt(publicationYear)))
                .thenReturn(Optional.of(existingBook));
        Mockito.when(repo.save(Mockito.any(Book.class))).thenReturn(Optional.of(existingBook));

        // Act
        Book addedCopy = bookService.add(bookTitle, bookAuthor, publicationYear);

        // Assert
        assertEquals(existingBook, addedCopy);
        assertEquals(2, addedCopy.getNumOfCopies());
        assertEquals(2, addedCopy.getAvailableCopies());

        Mockito.verify(repo, Mockito.times(1)).findByTitleAndAuthorAndPublicationYear(bookTitle, bookAuthor, Integer.parseInt(publicationYear));
        Mockito.verify(repo, Mockito.times(1)).save(Mockito.any(Book.class));
    }

    @Test
    public void testGetBooksByTitle() {
        // Arrange
        String searchQuery = "The Lord of the Rings";
        String searchField = "title";
        int pageNo = 1;
        String sortField = "author";
        String sortOrder = "desc";

        List<Book> expectedBooks = Arrays.asList(
                new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954),
                new Book("The Hobbit", "J.R.R. Tolkien", 1937)
        );

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo - 1, bookService.getPageSize(), sort);
        System.out.println(pageable);

        Mockito.when(repo.findByTitleContaining(searchQuery, pageable)).thenReturn(new PageImpl<>(List.of(expectedBooks.get(0)),
                pageable, 1));

        // Act
        Page<Book> actualBooks = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        // Assert
        assertEquals(1, actualBooks.getTotalElements());
        assertEquals(expectedBooks.get(0), actualBooks.getContent().get(0));
    }

    @Test
    public void testGetBooksByAuthor() {
        // Arrange
        String searchQuery = "J.R.R. Tolkien";
        String searchField = "author";
        int pageNo = 1;
        String sortField = "author";
        String sortOrder = "desc";

        List<Book> expectedBooks = Arrays.asList(
                new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954),
                new Book("The Hobbit", "J.R.R. Tolkien", 1937)
        );

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo - 1, bookService.getPageSize(), sort);
        System.out.println(pageable);

        Mockito.when(repo.findByAuthorContaining(searchQuery, pageable)).thenReturn(new PageImpl<>(expectedBooks,
                pageable, expectedBooks.size()));

        // Act
        Page<Book> actualBooks = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        // Assert
        assertEquals(expectedBooks.size(), actualBooks.getTotalElements());
        assertEquals(expectedBooks, actualBooks.getContent());
    }

    @Test
    public void testGetBooksWithEmptyQuery() {
        // Arrange
        String searchQuery = "";
        String searchField = "title";
        int pageNo = 1;
        String sortField = "author";
        String sortOrder = "asc";

        Page<Book> expectedBooks = new PageImpl<>(Collections.emptyList());

        Mockito.when(repo.findAll(Mockito.any(Pageable.class))).thenReturn(expectedBooks);

        // Act
        Page<Book> actualBooks = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        // Assert
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void testGetBooksWithInvalidField() {
        // Arrange
        String searchQuery = "The Lord of the Rings";
        String searchField = "invalid";
        int pageNo = 1;
        String sortField = "author";
        String sortOrder = "asc";

        Page<Book> expectedBooks = new PageImpl<>(Collections.emptyList());

        Mockito.when(repo.findByTitleContaining(Mockito.eq(searchQuery), Mockito.any(Pageable.class))).thenReturn(expectedBooks);

        // Act
        Page<Book> actualBooks = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        // Assert
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void testGetBooksWithInvalidPageNumberLess() {
        // Arrange
        String searchQuery = "The Lord of the Rings";
        String searchField = "title";
        int pageNo = -1;
        String sortField = "author";
        String sortOrder = "asc";

        Page<Book> expectedBooks = null;

        // Act
        Page<Book> actualBooks = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        // Assert
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void testGetBooksWithInvalidPageNumberMore() {
        // Arrange
        String searchQuery = "The Lord of the Rings";
        String searchField = "title";
        int pageNo = 100;
        String sortField = "author";
        String sortOrder = "asc";

        Page<Book> expectedBooks = null;

        Mockito.when(repo.findByTitleContaining(Mockito.eq(searchQuery), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        Page<Book> actualBooks = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        // Assert
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void testGetBooksWithInvalidSortOrder() {
        // Arrange
        String searchQuery = "The Lord of the Rings";
        String searchField = "title";
        int pageNo = 1;
        String sortField = "author";
        String sortOrder = "invalid";

        Page<Book> expectedBooks = new PageImpl<>(Collections.emptyList());

        Mockito.when(repo.findByTitleContaining(Mockito.eq(searchQuery), Mockito.any(Pageable.class))).thenReturn(expectedBooks);

        // Act
        Page<Book> actualBooks = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        // Assert
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void testGetById() {
        // Arrange
        Integer id = 1;
        Book expectedBook = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        expectedBook.setId(id);
        expectedBook.setNumOfCopies(1);
        expectedBook.setAvailableCopies(1);

        Mockito.when(repo.findById(id)).thenReturn(Optional.of(expectedBook));

        // Act
        Optional<Book> actualBook = bookService.findById(id);

        // Assert
        Assertions.assertTrue(actualBook.isPresent());
        assertEquals(expectedBook, actualBook.get());
    }

    @Test
    public void testGetPageSize() {
        // Arrange
        int expectedPageSize = 5;

        // Act
        int actualPageSize = bookService.getPageSize();

        // Assert
        assertEquals(expectedPageSize, actualPageSize);
    }
}

