package com.epam.library.controller;

import com.epam.library.service.BookService;
import com.epam.library.service.SubscriptionService;
import com.epam.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LibrarianController {

    private final UserService userService;

    private final SubscriptionService subscriptionService;

    private final BookService bookService;

    public LibrarianController(UserService userService, SubscriptionService subscriptionService,
                          BookService bookService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.bookService = bookService;
    }

    @GetMapping("/readers")
    public String readers(Model model) {
        model.addAttribute("users", userService.findAll());

        return "readers";
    }
}
