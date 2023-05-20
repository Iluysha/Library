package com.epam.library.unit.service;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.entity.User;
import com.epam.library.repository.SubscriptionRepository;
import com.epam.library.service.BookService;
import com.epam.library.service.SubscriptionService;
import com.epam.library.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepo;
    @Mock
    private UserService userService;
    @Mock
    private BookService bookService;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testSave() throws Exception {
        // Arrange
        Subscription subscription = new Subscription();
        Mockito.when(subscriptionRepo.findById(Mockito.any())).thenReturn(Optional.of(subscription));

        // Act
        subscriptionService.save(subscription);
        Subscription savedSubscription = subscriptionService.findById(subscription.getId());

        // Assert
        assertEquals(subscription, savedSubscription);
        Mockito.verify(subscriptionRepo, Mockito.times(1)).save(subscription);
    }

    @Test
    public void testFindById() throws Exception {
        // Arrange
        Subscription subscription = new Subscription();

        Mockito.when(subscriptionRepo.findById(Mockito.any())).thenReturn(Optional.of(subscription));

        // Act
        Subscription foundSubscription = subscriptionService.findById(subscription.getId());

        // Assert
        assertEquals(subscription, foundSubscription);
    }

    @Test
    public void testFindByUserEmail() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Subscription subscription = new Subscription(user, book);
        subscription.setUser(user);

        Mockito.when(subscriptionRepo.findByUserEmail(user.getEmail())).thenReturn(List.of(subscription));

        // Act
        List<Subscription> foundSubscriptions = subscriptionService.findByUserEmail(user.getEmail());

        // Assert
        assertTrue(foundSubscriptions.contains(subscription));
    }

    @Test
    public void testFindAll() {
        // Arrange
        Subscription subscription = new Subscription();

        Mockito.when(subscriptionRepo.findAll()).thenReturn(List.of(subscription));

        // Act
        List<Subscription> foundSubscriptions = subscriptionService.findAll();

        // Assert
        assertTrue(foundSubscriptions.contains(subscription));
    }

    @Test
    public void testOrderBook() throws Exception {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        int bookId = 10;
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Book updatedBook = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        int availableCopies = 5;
        book.setAvailableCopies(availableCopies);
        updatedBook.setAvailableCopies(availableCopies - 1);
        Subscription subscription = new Subscription(user, updatedBook);

        Mockito.when(bookService.findById(bookId)).thenReturn(book);
        Mockito.when(userService.findByEmail(user.getEmail())).thenReturn(user);

        // Act
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());
        Subscription newSubscription = subscriptionService.orderBook(new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        ), bookId);

        // Assert
        assertEquals(subscription, newSubscription);
        assertEquals(user, newSubscription.getUser());
        assertEquals(updatedBook, newSubscription.getBook());
    }

    @Test
    public void testOrderBook_BookNotFound() throws Exception {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        int bookId = 10;

        Mockito.when(bookService.findById(bookId)).thenThrow(new Exception());
        Mockito.when(userService.findByEmail(user.getEmail())).thenReturn(new User());

        // Assert
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());
        assertThrows(Exception.class, () -> subscriptionService.orderBook(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singletonList(authority)
        ), bookId));
    }

    @Test
    public void testApproveSubscription() throws Exception {
        // Arrange
        Subscription subscription = new Subscription();
        subscription.setApproved(false);

        Mockito.when(subscriptionRepo.findById(Mockito.any())).thenReturn(Optional.of(subscription));

        // Save subscription
        subscriptionService.save(subscription);

        // Assert that the subscription is not approved
        assertFalse(subscription.isApproved());

        // Approve the subscription
        subscriptionService.approveSubscription(subscription.getId());

        // Assert that the subscription is approved
        assertTrue(subscription.isApproved());
    }

    @Test
    public void testCalculateAndAddFines() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        Book book = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Subscription subscription = new Subscription(user, book);
        subscription.setStartDate(LocalDate.now().minusDays(80));

        Mockito.when(subscriptionRepo.findAll()).thenReturn(List.of(subscription));

        // Act
        subscriptionService.calculateAndAddFines();

        // Assert
        assertEquals(10, subscription.getFine());
    }
}
