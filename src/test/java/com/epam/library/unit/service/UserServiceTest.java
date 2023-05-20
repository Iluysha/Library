package com.epam.library.unit.service;

import com.epam.library.entity.User;
import com.epam.library.repository.UserRepository;
import com.epam.library.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository repo;
    @Mock
    public PasswordEncoder mockedPasswordEncoder;
    @InjectMocks
    private UserService userService;

    public PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testFindById() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        user.setId(1);
        Mockito.when(repo.findById(anyInt())).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findById(user.getId());

        // Assert
        assertEquals(user, foundUser);
    }

    @Test
    public void testFindByEmail() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        Mockito.when(repo.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findByEmail("johndoe@example.com");

        // Assert
        assertEquals(user, foundUser);
    }

    @Test
    public void testSave() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        Mockito.when(repo.save(user)).thenReturn(user);

        // Act
        userService.save(user);

        // Assert
        Mockito.verify(repo, Mockito.times(1)).save(user);
    }

    @Test
    public void testRegisterUserNotRegistered() throws Exception {
        // Arrange
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password";

        Mockito.when(mockedPasswordEncoder.encode(password)).thenReturn(passwordEncoder.encode(password));
        Mockito.when(repo.findByEmail(email)).thenReturn(Optional.empty());

        User user = new User(name, email, mockedPasswordEncoder.encode(password), User.Role.READER);

        // Act
        User registeredUser = userService.register(name, email, password);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(user, registeredUser);
        Mockito.verify(repo).save(registeredUser);
    }

    @Test
    public void testRegisterUser_alreadyRegistered() {
        // Arrange
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password";
        User existingUser = new User(name, email, mockedPasswordEncoder.encode(password), User.Role.READER);

        Mockito.when(repo.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // Assert
        assertThrows(Exception.class, () -> userService.register(name, email, password));
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testRegister_invalidInput() {
        // Arrange
        String name = "";
        String email = "invalid-email";
        String password = "";

        // Assert
        assertThrows(Exception.class, () -> userService.register(name, email, password));
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testFindAllNotAdmin() {
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        Mockito.when(repo.findByRoleNot(User.Role.ADMIN)).thenReturn(List.of(user));

        List<User> users = userService.findAllNotAdmin();

        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

    @Test
    public void testBlockUser() {
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        user.setId(1);
        Mockito.when(repo.findById(anyInt())).thenReturn(Optional.of(user));

        userService.blockUser(user.getId());

        Mockito.verify(repo, Mockito.times(1)).save(user);
    }

    @Test
    public void testBlockUserNotFound() {
        // Arrange
        Mockito.when(repo.findById(anyInt())).thenReturn(Optional.empty());

        //Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.blockUser(1));
    }

    @Test
    public void testLoadUserByUsernameFound() throws UsernameNotFoundException {
        User user = new User("John Doe", "johndoe@example.com", "password", User.Role.READER);
        Mockito.when(repo.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("johndoe@example.com");

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsernameNotFound() throws UsernameNotFoundException {
        String username = "johndoe@example.com";
        Mockito.when(repo.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }
}