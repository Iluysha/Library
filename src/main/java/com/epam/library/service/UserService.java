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
import java.util.Optional;
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
     * @return An Optional containing the found user, or an empty Optional if not found.
     */
    public Optional<User> findById(Integer id) {
        log.info("Finding user by id: {}", id);
        return repo.findById(id);
    }

    /**
     * Finds a user by their email.
     *
     * @param email  The email of the user to find.
     * @return An Optional containing the found user, or an empty Optional if not found.
     */
    public Optional<User> findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return repo.findByEmail(email);
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
     * @return The registered user, or null if registration fails.
     */
    @Transactional
    public User register(String name, String email, String password) {
        log.info("Registering user with email: {}", email);

        if(findByEmail(email).isPresent()) {
            log.info("Email {} already registered", email);
            return null;
        } else {
            if(Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches() &&
                    !name.equals("") && !password.equals("")) {
                // Create and save a new user object
                User user = new User(name, email, passwordEncoder.encode(password), User.Role.READER);
                save(user);

                log.info("User registered successfully: {}", user);
                return user;
            } else {
                log.error("Wrong input: {}, {}, {}", name, email, password);
                return null;
            }
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
     * @return The blocked/unblocked user, or null if the user was not found.
     */
    @Transactional
    public User blockUser(Integer id) {
        Optional<User> optionalUser = findById(id);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBlocked(!user.isBlocked());
            save(user);

            log.info("User {} blocked", user);
            return user;
        } else {
            log.error("User {} not found", id);
            return null;
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
        Optional<User> optionalUser = findByEmail(username);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();

            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(authority)
            );
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
