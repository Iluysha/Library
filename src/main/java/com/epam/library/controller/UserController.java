package com.epam.library.controller;

import com.epam.library.entity.User;
import com.epam.library.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {

    private static final Logger log = LogManager.getLogger(BookController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        log.info("Received request for register page");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               RedirectAttributes attributes) {
        log.info("Registering user with email: {}", email);

        // Check if the email is already registered
        if (userService.findByEmail(email).isPresent()) {
            log.info("Email {} already registered", email);
            attributes.addFlashAttribute("error", true);
            return "redirect:/register";
        }

        // Create a new user object
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.READER);

        // Save the user
        userService.save(user);

        log.info("User registered successfully: {}", user);
        attributes.addFlashAttribute("signup", true);
        return "redirect:/login";
    }

    @GetMapping("/account")
    public String account(@AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes attributes,
                          Model model) {
        // Retrieve the authenticated user's details
        Optional<User> user = userService.findByEmail(userDetails.getUsername());

        if(user.isPresent()) {
            log.info("Handling account request for user: {}", user.get().getName());
            model.addAttribute("user", user.get());
            return "account";
        } else {
            attributes.addFlashAttribute("msg_code", "user_not_found");
            return "redirect:/error";
        }
    }

    @GetMapping("/users")
    public String readers(Model model) {
        log.info("Handling users request");

        model.addAttribute("users", userService.findAllNotAdmin());

        return "users";
    }

    @PostMapping("/block")
    public String blockUser(@RequestParam("userId") Integer id,
                            RedirectAttributes attributes) {
        Optional<User> optionalUser = userService.findById(id);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBlocked(!user.isBlocked());
            userService.save(user);

            return "redirect:users";
        } else {
            attributes.addFlashAttribute("msg_code", "user_not_found");
            return "redirect:/error";
        }
    }
}