package com.epam.library.unit.controller;

import com.epam.library.controller.UserController;
import com.epam.library.entity.User;
import com.epam.library.service.UserService;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes attributes;
    @InjectMocks
    private UserController controller;

    @Test
    public void testRegisterPage() {
        // Arrange
        String expectedViewName = "register";

        // Act
        String actualViewName = controller.registerPage();

        // Assert
        assertEquals(expectedViewName, actualViewName);
    }

    @Test
    public void testRegisterUser() {
        // Arrange
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password123";

        User user = new User(name, email, password, User.Role.READER);

        Mockito.when(userService.register(name, email, password)).thenReturn(user);

        // Act
        String actualViewName = controller.registerUser(name, email, password, attributes);

        // Assert
        assertEquals("redirect:/login", actualViewName);
        Mockito.verify(attributes).addFlashAttribute("signup", true);
    }

    @Test
    public void testRegisterExistingUser() {
        // Arrange
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password123";

        Mockito.when(userService.register(name, email, password)).thenReturn(null);

        // Act
        String actualViewName = controller.registerUser(name, email, password, attributes);

        // Assert
        assertEquals("redirect:/register", actualViewName);
        Mockito.verify(attributes).addFlashAttribute("error", true);
    }

    @Test
    public void testAccount() {
        // Arrange
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password123";

        User user = new User(name, email, password, User.Role.READER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
                ));

        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        String actualViewName = controller.account(userDetails, attributes, model);

        // Assert
        assertEquals("account", actualViewName);
        Mockito.verify(model).addAttribute("user", user);
    }

    @Test
    public void testReaders() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(new User());

        Mockito.when(userService.findAllNotAdmin()).thenReturn(users);

        // Act
        String actualViewName = controller.users(model);

        // Assert
        assertEquals("users", actualViewName);
        Mockito.verify(model).addAttribute("users", users);
    }

    @Test
    public void testBlockUser() {
        // Arrange
        Integer id = 1;

        User user = new User();
        user.setId(id);

        Mockito.when(userService.blockUser(id)).thenReturn(user);

        // Act
        String actualViewName = controller.blockUser(id, attributes);

        // Assert
        assertEquals("redirect:users", actualViewName);
    }
}

