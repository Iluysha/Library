package com.epam.library.controller;

import com.epam.library.service.BookService;
import com.epam.library.service.SubscriptionService;
import com.epam.library.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String account(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        model.addAttribute("user", userService.findByEmail(userDetails.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User not found")));

        return "account";
    }

    @GetMapping("/readers")
    public String readers(Model model) {
        model.addAttribute("users", userService.findAll());

        return "readers";
    }

    @GetMapping("/librarians")
    public String librarians(Model model) {
        model.addAttribute("librarians", userService.findAll());

        return "librarians";
    }
}
