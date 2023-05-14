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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

//Service for crud operations with users
@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LogManager.getLogger(UserService.class);
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findById(Integer id) {
        log.info("Finding user by id: {}", id);
        return repo.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return repo.findByEmail(email);
    }

    public void save(User user) {
        log.info("Saving user: {}", user);
        repo.save(user);
    }

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

    //Get a list of all users
    public List<User> findAllNotAdmin() {
        log.info("Finding all users who are not administrators");
        return repo.findByRoleNot(User.Role.ADMIN);
    }

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
