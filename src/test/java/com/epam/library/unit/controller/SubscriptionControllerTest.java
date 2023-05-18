package com.epam.library.unit.controller;

import com.epam.library.controller.SubscriptionController;
import com.epam.library.entity.Subscription;
import com.epam.library.entity.User;
import com.epam.library.service.SubscriptionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes attributes;
    @InjectMocks
    private SubscriptionController controller;

    @Test
    public void testSubscriptionsAdmin() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.ADMIN);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
                ));

        String expectedViewName = "librarian_subscriptions";
        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(new Subscription());

        Mockito.when(subscriptionService.findAll()).thenReturn(subscriptions);

        // Act
        String actualViewName = controller.subscriptions(userDetails, model);

        // Assert
        assertEquals(expectedViewName, actualViewName);
        Mockito.verify(model).addAttribute("subscriptions", subscriptions);
    }

    @Test
    public void testSubscriptionsReader() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
                ));

        String expectedViewName = "reader_subscriptions";
        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(new Subscription());

        Mockito.when(subscriptionService.findByUserEmail(userDetails.getUsername())).thenReturn(subscriptions);

        // Act
        String actualViewName = controller.subscriptions(userDetails, model);

        // Assert
        assertEquals(expectedViewName, actualViewName);
        Mockito.verify(model).addAttribute("subscriptions", subscriptions);
    }

    @Test
    public void testOrderSuccess() {
        // Arrange
        String expectedViewName = "order";

        // Act
        String actualViewName = controller.orderSuccess();

        // Assert
        assertEquals(expectedViewName, actualViewName);
    }

    @Test
    public void testOrderBook() throws Exception {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
                ));

        String expectedViewName = "redirect:order";
        Integer bookId = 1;
        Subscription subscription = new Subscription();

        Mockito.when(subscriptionService.orderBook(userDetails, bookId)).thenReturn(subscription);

        // Act
        String actualViewName = controller.orderBook(userDetails, bookId, attributes);

        // Assert
        assertEquals(expectedViewName, actualViewName);
    }

    @Test
    public void testOrderBookError() throws Exception {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
                ));

        String expectedViewName = "redirect:error";
        Integer bookId = 1;

        Mockito.when(subscriptionService.orderBook(userDetails, bookId)).thenThrow(new Exception());

        // Act
        String actualViewName = controller.orderBook(userDetails, bookId, attributes);

        // Assert
        assertEquals(expectedViewName, actualViewName);
        Mockito.verify(attributes).addFlashAttribute("msg_code", "no_such_book");
    }

    @Test
    public void testApproveSubscription() throws Exception {
        // Arrange
        String expectedViewName = "redirect:subscriptions";
        Integer subscriptionId = 1;
        Subscription subscription = new Subscription();

        Mockito.when(subscriptionService.approveSubscription(subscriptionId)).thenReturn(subscription);

        // Act
        String actualViewName = controller.approveSubscription(subscriptionId, attributes);

        // Assert
        assertEquals(expectedViewName, actualViewName);
    }

    @Test
    public void testApproveSubscriptionError() throws Exception {
        // Arrange
        String expectedViewName = "redirect:error";
        Integer subscriptionId = 1;

        Mockito.when(subscriptionService.approveSubscription(subscriptionId))
                .thenThrow(new Exception("Failed to approve subscription with id: " + subscriptionId));

        // Act
        String actualViewName = controller.approveSubscription(subscriptionId, attributes);

        // Assert
        assertEquals(expectedViewName, actualViewName);
        Mockito.verify(attributes).addFlashAttribute("msg_code", "cant_approve");
    }
}