package com.epam.library.controller;

import com.epam.library.entity.Book;
import com.epam.library.service.BookService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testBooks() throws Exception {
        // Prepare test data
        List<Book> books = new ArrayList<>();

        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setPublicationYear(2001);
        book1.setNumOfCopies(3);
        book1.setAvailableCopies(1);

        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setPublicationYear(2002);
        book2.setNumOfCopies(2);
        book2.setAvailableCopies(0);

        books.add(book1);
        books.add(book2);
        Page<Book> page = new PageImpl<>(books);
        when(bookService.getBooks(anyInt(), anyString(), anyString())).thenReturn(page);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attribute("books", books))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("sortField", "title"))
                .andExpect(model().attribute("sortOrder", "asc"));

        // Verify that the bookService.getBooks method was called with the correct arguments
        verify(bookService).getBooks(1, "title", "asc");
    }

    @Test
    public void testAddBook() throws Exception {
        // Perform the POST request and assert the response
        mockMvc.perform(post("/add-book")
                        .param("bookTitle", "Book 1")
                        .param("bookAuthor", "Author 1")
                        .param("publicationYear", "2021"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        // Verify that the bookService.add method was called with the correct Book object
        verify(bookService).add(argThat(book ->
                book.getTitle().equals("Book 1") &&
                        book.getAuthor().equals("Author 1") &&
                        book.getPublicationYear() == 2021));
    }
}
