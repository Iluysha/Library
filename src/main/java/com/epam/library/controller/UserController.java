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

@Controller
public class UserController {

    private static final Logger log = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
        if(userService.register(name, email, password) == null) {
            attributes.addFlashAttribute("error", true);
            return "redirect:/register";
        } else {
            attributes.addFlashAttribute("signup", true);
            return "redirect:/login";
        }
    }

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
        log.info("Blocking user with ID: {}", id);

        if(userService.blockUser(id) != null) {
            return "redirect:users";
        } else {
            log.warn("User {} not found", id);
            attributes.addFlashAttribute("msg_code", "user_not_found");
            return "redirect:/error";
        }
    }
}