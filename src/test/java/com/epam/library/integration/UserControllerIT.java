package com.epam.library.integration;

import com.epam.library.entity.User;
import com.epam.library.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    @Transactional
    public void testRegisterUser_ValidInput() throws Exception {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);

        // Perform the request and validate the response
        mockMvc.perform(post("/register")
                        .param("name", user.getName())
                        .param("email", user.getEmail())
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("signup"));

        // Clean up the test user
        userRepository.deleteByEmail(user.getEmail());
    }

    @Test
    public void testRegisterUser_EmailAlreadyRegistered() throws Exception {
        // Arrange
        User existingUser = new User("John Doe", "johndoe@example.com",
                passwordEncoder.encode("password"), User.Role.READER);
        userRepository.save(existingUser);

        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password";

        // Perform the request and validate the response
        mockMvc.perform(post("/register")
                        .param("name", name)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("error"));

        // Clean up the test user
        userRepository.delete(existingUser);
    }
}