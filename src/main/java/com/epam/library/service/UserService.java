package com.epam.library.service;

import com.epam.library.controller.BookController;
import com.epam.library.entity.User;
import com.epam.library.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

//Service for crud operations with users
@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LogManager.getLogger(BookController.class);
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void save(User user) {
        log.info("Saving user: {}", user);

        repo.save(user);
    }

    //Get a list of all users
    public List<User> findAll() {
        log.info("Finding all users");

        return (List<User>) repo.findAll();
    }

    public Optional<User> findByEmail(String email) {
        log.info("Finding user by email: {}", email);

        return repo.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);

        User user = repo.findByEmail(username).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
