package com.epam.library.integration;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application_integration.properties")
public class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;

    @After
    @BeforeEach
    public void cleanDatabase() {
        bookRepository.deleteAll();
    }

    @Test
    public void testBooks_ValidInput() throws Exception {
        // Create the books
        Book book1 = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien", 1937);
        bookRepository.save(book1);
        bookRepository.save(book2);

        // Perform the request and validate the response
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attribute("books", hasItem(
                        allOf(
                                hasProperty("title", is("The Lord of the Rings")),
                                hasProperty("author", is("J.R.R. Tolkien")))
                        )))
                .andExpect(model().attribute("books", hasItem(
                        allOf(
                                hasProperty("title", is("The Hobbit")),
                                hasProperty("author", is("J.R.R. Tolkien"))
                        )))
                );
    }

    @Test
    public void testBooks_InvalidInput() throws Exception {
        // Create the books
        Book book1 = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien", 1937);
        bookRepository.save(book1);
        bookRepository.save(book2);

        // Perform the request and validate the response
        mockMvc.perform(get("/books")
                        .param("pageNo", String.valueOf(100)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("error"))
                .andExpect(flash().attributeExists("msg_code"))
                .andExpect(flash().attribute("msg_code", "invalid_page"));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"ADMIN"})
    public void testAddBookPage() throws Exception {
        // Perform the request and validate the response
        mockMvc.perform(get("/add-book"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-book"));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"ADMIN"})
    public void testAddBook_ValidInput() throws Exception {
        // Arrange
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);

        // Perform the request and validate the response
        mockMvc.perform(post("/add-book")
                        .param("bookTitle", book.getTitle())
                        .param("bookAuthor", book.getAuthor())
                        .param("publicationYear", book.getPublicationYear().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("books"));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"ADMIN"})
    public void testAddBook_InvalidInput() throws Exception {
        // Arrange
        String bookTitle = "Invalid Book";
        String bookAuthor = "Invalid Author";
        String publicationYear = "Invalid Year";

        // Perform the request and validate the response
        mockMvc.perform(post("/add-book")
                        .param("bookTitle", bookTitle)
                        .param("bookAuthor", bookAuthor)
                        .param("publicationYear", publicationYear))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("error"))
                .andExpect(flash().attributeExists("msg_code"))
                .andExpect(flash().attribute("msg_code", "invalid_input"));
    }
}