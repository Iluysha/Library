package com.epam.library.controller;

import com.epam.library.entity.User;
import com.epam.library.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller class for managing user-related operations.
 */
@Controller
public class UserController {

    private static final Logger log = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles the GET request to the /register URL.
     * Returns the "register" page for user registration.
     *
     * @return The view name for rendering the "register" page.
     */
    @GetMapping("/register")
    public String registerPage() {
        log.info("Received request for register page");
        return "register";
    }

    /**
     * Handles the POST request to the /register URL.
     * Registers a new user with the specified name, email, and password.
     * If the registration is successful, redirects to the "login" page with a signup success flash attribute.
     * Otherwise, redirects to the "register" page with an error flash attribute.
     *
     * @param name       The name of the user.
     * @param email      The email of the user.
     * @param password   The password of the user.
     * @param attributes RedirectAttributes for adding flash attributes.
     * @return The view name for redirection.
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               RedirectAttributes attributes) {
        // Check if the email is already registered
        if(userService.register(name, email, password) == null) {
            attributes.addFlashAttribute("error", true);
            return "redirect:register";
        } else {
            attributes.addFlashAttribute("signup", true);
            return "redirect:login";
        }
    }

    /**
     * Handles the GET request to the /account URL.
     * Retrieves the account information for the logged-in user and returns the "account" page.
     * If the user is found, adds the user object to the model.
     * Otherwise, redirects to the "error" page with a flash attribute indicating the failure reason.
     *
     * @param userDetails The UserDetails of the logged-in user.
     * @param attributes  RedirectAttributes for adding flash attributes.
     * @param model       The Model object for passing data to the view.
     * @return The view name for rendering the "account" page.
     */
    @GetMapping("/account")
    public String account(@AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes attributes,
                          Model model) {
        log.info("Handling account request for user: {}", userDetails.getUsername());
        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());

        if(optionalUser.isPresent()){
            model.addAttribute("user", optionalUser.get());
            return "account";
        } else {
            log.warn("User {} not found", userDetails.getUsername());
            attributes.addFlashAttribute("msg_code", "user_not_found");
            return "redirect:error";
        }
    }

    /**
     * Handles the GET request to the /users URL.
     * Retrieves a list of non-admin users and returns the "users" page.
     *
     * @param model The Model object for passing data to the view.
     * @return The view name for rendering the "users" page.
     */
    @GetMapping("/users")
    public String users(Model model) {
        log.info("Handling users request");
        model.addAttribute("users", userService.findAllNotAdmin());
        return "users";
    }

    /**
     * Handles the POST request to the /block URL.
     * Blocks a user with the specified user ID.
     * If the user is blocked successfully, redirects to the "users" page.
     * Otherwise, redirects to the "error" page with a flash attribute indicating the failure reason.
     *
     * @param id          The ID of the user to block.
     * @param attributes  RedirectAttributes for adding flash attributes.
     * @return The view name for redirection.
     */
    @PostMapping("/block")
    public String blockUser(@RequestParam("userId") Integer id,
                            RedirectAttributes attributes) {
        log.info("Blocking user with ID: {}", id);

        if(userService.blockUser(id) != null) {
            return "redirect:users";
        } else {
            log.warn("User {} not found", id);
            attributes.addFlashAttribute("msg_code", "user_not_found");
            return "redirect:error";
        }
    }
}