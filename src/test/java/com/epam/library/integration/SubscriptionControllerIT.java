package com.epam.library.integration;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.entity.User;
import com.epam.library.repository.BookRepository;
import com.epam.library.repository.SubscriptionRepository;
import com.epam.library.repository.UserRepository;
import org.junit.Test;
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

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"READER"})
    public void testSubscriptionsAsReader() throws Exception {
        // Create the subscription
        User user = new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);
        userRepository.save(user);
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        bookRepository.save(book);
        Subscription subscription = subscriptionRepository.save(new Subscription(user, book));

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

        // Clean up the test subscription
        subscriptionRepository.delete(subscription);
        userRepository.delete(user);
        bookRepository.delete(book);
    }

    @Test
    @WithMockUser(username = "jackdoe@example.com", roles = {"LIBRARIAN"})
    public void testSubscriptionsAsLibrarian() throws Exception {
        // Create the subscriptions
        User userReader = new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);
        User userLibrarian = new User("Jack Doe", "jackdoe@example.com",
                passwordEncoder.encode("password"), User.Role.LIBRARIAN);
        userRepository.save(userReader);
        userRepository.save(userLibrarian);
        Book book1 = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien", 1937);
        bookRepository.save(book1);
        bookRepository.save(book2);
        Subscription subscriptionReader = subscriptionRepository.save(new Subscription(userReader, book1));
        Subscription subscriptionLibrarian = subscriptionRepository.save(new Subscription(userLibrarian, book2));

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

        // Clean up the test data
        subscriptionRepository.delete(subscriptionReader);
        subscriptionRepository.delete(subscriptionLibrarian);
        bookRepository.delete(book1);
        bookRepository.delete(book2);
        userRepository.delete(userReader);
        userRepository.delete(userLibrarian);
    }
}