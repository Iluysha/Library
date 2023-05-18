package com.epam.library.service;

import com.epam.library.entity.User;
import com.epam.library.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service class for managing user-related operations.
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LogManager.getLogger(UserService.class);
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Finds a user by their ID.
     *
     * @param id  The ID of the user to find.
     * @return the user if found, or throws exception otherwise.
     */
    public User findById(Integer id) {
        log.info("Finding user by id: {}", id);
        return repo.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Finds a user by their email.
     *
     * @param email  The email of the user to find.
     * @return the user if found, or throws exception otherwise.
     */
    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Saves a user.
     *
     * @param user  The user to save.
     */
    public void save(User user) {
        log.info("Saving user: {}", user);
        repo.save(user);
    }

    /**
     * Registers a new user with the specified name, email, and password.
     *
     * @param name     The name of the user.
     * @param email    The email of the user.
     * @param password The password of the user.
     * @return The registered user, or throws exception if registration fails.
     */
    @Transactional
    public User register(String name, String email, String password) throws Exception {
        log.info("Registering user with email: {}", email);

        try {
            findByEmail(email);
            log.warn("Email {} already registered", email);
            throw new Exception("Email " + email + " already registered");
        } catch (UsernameNotFoundException e) {
            if(!Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches() ||
                    name.equals("") && password.equals("")) {
                log.error("Wrong input: {}, {}, {}", name, email, password);
                throw new Exception("Wrong input: " + name + email + password);
            }

            // Create and save a new user object
            User user = new User(name, email, passwordEncoder.encode(password), User.Role.READER);
            save(user);

            log.info("User registered successfully: {}", user);
            return user;
        }
    }

    /**
     * Retrieves a list of all users who are not administrators.
     *
     * @return A list of all non-administrator users.
     */
    public List<User> findAllNotAdmin() {
        log.info("Finding all users who are not administrators");
        return repo.findByRoleNot(User.Role.ADMIN);
    }

    /**
     * Blocks or unblocks a user with the specified ID.
     *
     * @param id  The ID of the user to block/unblock.
     * @return The blocked/unblocked user, or throws exception if the user was not found.
     */
    @Transactional
    public User blockUser(Integer id) {

        try {
            User user = findById(id);
            user.setBlocked(!user.isBlocked());
            save(user);

            log.info("User {} blocked", user);
            return user;
        } catch (Exception e) {
            log.error("User {} not found", id);
            throw e;
        }
    }

    /**
     * Loads a user by their username (email).
     * Method is used for Spring Security authentication.
     *
     * @param username  The username (email) of the user to load.
     * @return A UserDetails object representing the user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByEmail(username);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
