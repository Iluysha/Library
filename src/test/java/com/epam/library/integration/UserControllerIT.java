package com.epam.library.integration;

import com.epam.library.entity.User;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application_integration.properties")
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @After
    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterPage() throws Exception {
        // Perform the request and validate the response
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"READER"})
    public void testAccount_valid() throws Exception {
        // Arrange
        User user = userRepository.save(new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER));

        // Perform the request and validate the response
        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("account"))
                .andExpect(model().attribute("user",
                        hasProperty("email", is(user.getEmail())))
                );
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"READER"})
    public void testAccount_invalidUser() throws Exception {
        // Perform the request and validate the response
        mockMvc.perform(get("/account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("error"))
                .andExpect(flash().attributeExists("msg_code"))
                .andExpect(flash().attribute("msg_code", "user_not_found"));
    }

    @Test
    public void testRegisterUser_validInput() throws Exception {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);

        // Perform the request and validate the response
        mockMvc.perform(post("/register")
                        .param("name", user.getName())
                        .param("email", user.getEmail())
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"))
                .andExpect(flash().attributeExists("signup"));
    }

    @Test
    public void testRegisterUser_emailAlreadyRegistered() throws Exception {
        // Arrange
        User user = userRepository.save(new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER));

        // Perform the request and validate the response
        mockMvc.perform(post("/register")
                        .param("name", user.getName())
                        .param("email", user.getEmail())
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("register"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "johndoe@example.com", roles = {"ADMIN"})
    public void testUsers() throws Exception {
        // Arrange
        userRepository.save(new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.ADMIN));
        User user = userRepository.save(new User("Jack Doe", "jackdoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER));

        // Perform the request and validate the response
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("users",
                        hasItem(hasProperty("email", is(user.getEmail()))))
                );
    }
}