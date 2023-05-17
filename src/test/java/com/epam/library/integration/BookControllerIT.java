package com.epam.library.integration;

import com.epam.library.entity.Book;
import com.epam.library.repository.BookRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application_integration.properties")
public class BookControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testBooks_ValidInput() {
        // Prepare test data
        List<Book> expectedBooks = Arrays.asList(
                new Book("Harry Potter 10", "J. K. Rowling", 2003),
                new Book("Harry Potter 11", "J. K. Rowling", 2003)
        );

        // Perform GET request to /books
        ResponseEntity<String> response = restTemplate.getForEntity("/books", String.class);

        // Assert the response status code and content
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Books"));

        // Assert the presence of expected books in the response content
        for (Book expectedBook : expectedBooks) {
            assertTrue(response.getBody().contains(expectedBook.getTitle()));
            assertTrue(response.getBody().contains(expectedBook.getAuthor()));
        }
    }

    @Test
    public void testBooks_InvalidInput() throws Exception {
        mockMvc.perform(get("/books")
                        .param("pageNo", String.valueOf(100)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"))
                .andExpect(flash().attributeExists("msg_code"))
                .andExpect(flash().attribute("msg_code", "invalid_page"));
    }

    @Test
    @Transactional
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
                .andExpect(redirectedUrl("/books"));

        bookRepository.deleteByTitle("The Lord of the Rings");
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
                .andExpect(redirectedUrl("/error"))
                .andExpect(flash().attributeExists("msg_code"))
                .andExpect(flash().attribute("msg_code", "invalid_input"));
    }
}