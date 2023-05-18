package com.epam.library.integration;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.entity.User;
import com.epam.library.repository.BookRepository;
import com.epam.library.repository.SubscriptionRepository;
import com.epam.library.repository.UserRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class SubscriptionControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @After
    @BeforeEach
    public void cleanDatabase() {
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"READER"})
    public void testSubscriptionsAsReader() throws Exception {
        // Create the subscription
        User user = new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);
        userRepository.save(user);
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        bookRepository.save(book);
        subscriptionRepository.save(new Subscription(user, book));

        // Perform the request and validate the response
        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("reader_subscriptions"))
                .andExpect(model().attribute("subscriptions", hasItem(
                        allOf(
                                hasProperty("user",
                                        hasProperty("email", is("johndoe@example.com"))),
                                hasProperty("book",
                                        hasProperty("title", is("The Lord of the Rings")))
                        )
                )));
    }

    @Test
    @WithMockUser(username = "jackdoe@example.com", roles = {"LIBRARIAN"})
    public void testSubscriptionsAsLibrarian() throws Exception {
        // Create the subscriptions
        User userReader = userRepository.save(new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER));
        User userLibrarian = userRepository.save(new User("Jack Doe", "jackdoe@example.com",
                passwordEncoder.encode("password"), User.Role.LIBRARIAN));
        Book book1 = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien", 1937);
        bookRepository.save(book1);
        bookRepository.save(book2);
        subscriptionRepository.save(new Subscription(userReader, book1));
        subscriptionRepository.save(new Subscription(userLibrarian, book2));

        // Perform the request and validate the response
        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("librarian_subscriptions"))
                .andExpect(model().attribute("subscriptions", hasItem(
                        allOf(
                                hasProperty("user",
                                        hasProperty("email", is("johndoe@example.com"))),
                                hasProperty("book",
                                        hasProperty("title", is("The Lord of the Rings")))
                        )
                )))
                .andExpect(model().attribute("subscriptions", hasItem(
                        allOf(
                                hasProperty("user",
                                        hasProperty("email", is("jackdoe@example.com"))),
                                hasProperty("book",
                                        hasProperty("title", is("The Hobbit")))
                        )
                )));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"READER"})
    public void testOrderBook() throws Exception {
        // Create the user and book
        userRepository.save(new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER));
        Book book = bookRepository.save(new
                Book("The Lord of the Rings", "J.R.R. Tolkien", 1954)).get();

        // Perform the request and validate the response
        mockMvc.perform(post("/order")
                        .param("bookId", book.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("order"));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"LIBRARIAN"})
    public void testSubscriptionsApprove() throws Exception {
        // Create the subscription
        User user = new User("Jack Doe", "jackdoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);
        userRepository.save(user);
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        bookRepository.save(book);
        Subscription subscription = subscriptionRepository.save(new Subscription(user, book));

        // Perform the request and validate the response
        mockMvc.perform(post("/approve")
                        .param("subscriptionId", subscription.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("subscriptions"));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"LIBRARIAN"})
    public void testSubscriptionsApprove_invalid() throws Exception {
        // Create the subscription
        User user = new User("Jack Doe", "jackdoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);
        userRepository.save(user);
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        bookRepository.save(book);
        Subscription subscription = new Subscription(user, book);
        subscription.setApproved(true);
        subscriptionRepository.save(subscription);

        // Perform the request and validate the response
        mockMvc.perform(post("/approve")
                        .param("subscriptionId", subscription.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("error"))
                .andExpect(flash().attributeExists("msg_code"))
                .andExpect(flash().attribute("msg_code", "cant_approve"));
    }
}