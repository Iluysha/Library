package com.epam.library.controller;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.service.BookService;
import com.epam.library.service.SubscriptionService;
import com.epam.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Controller
public class AdminController {

    private final UserService userService;

    private final SubscriptionService subscriptionService;

    private final BookService bookService;

    public AdminController(UserService userService, SubscriptionService subscriptionService,
                          BookService bookService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.bookService = bookService;
    }

    @GetMapping("/librarians")
    public String librarians(Model model) {
        model.addAttribute("librarians", userService.findAll());

        return "librarians";
    }

    @GetMapping("/add-book")
    public String addBookPage() {

        return "add-book";
    }

    @PostMapping("/add-book")
    public String addBook(@RequestParam("bookName") String bookName,
                          @RequestParam("bookAuthor") String bookAuthor,
                          @RequestParam("publicationDate") String publicationYear) {
        System.out.println("hello");

        Book newBook = new Book();
        newBook.setName(bookName);
        newBook.setAuthor(bookAuthor);
        newBook.setPublicationYear(Integer.valueOf(publicationYear));

        bookService.add(newBook);

        return "redirect:/books";
    }
}
