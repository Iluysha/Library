package com.epam.library.controller;

import com.epam.library.service.BookService;
import com.epam.library.service.SubscriptionService;
import com.epam.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserService userService;

    private final SubscriptionService subscriptionService;

    private final BookService bookService;

    public UserController(UserService userService, SubscriptionService subscriptionService,
                          BookService bookService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.bookService = bookService;
    }

    /*
        Method, handling get request to the /orders URL.
    */
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", subscriptionService.findAll());

        return "orders";
    }
}
