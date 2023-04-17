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

import java.util.Date;
import java.util.Optional;

@Controller
public class BookController {

    private final UserService userService;

    private final SubscriptionService subscriptionService;

    private final BookService bookService;

    public BookController(UserService userService, SubscriptionService subscriptionService,
                          BookService bookService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.bookService = bookService;
    }

    /*
        Method, handling get request to the /books URL.
        Gets the list of books in the library and returns the page ´books´,
        which prints this list with possibility of ordering.
    */
    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books", bookService.findAll());

        return "books";
    }

    /*
        Method, handling get request to the /order URL after user press ´Order´ button.
        Gets the id of the book from the request, checks if this book exists in the library
        and there are available copies. Then reduce number of available copies by one and saves the book.
        Then returns order page with the message about the order.
        If there is no such a book in the library or no available copies, redirects to the error page.
    */
    @PostMapping("/order")
    public String orderBook(@RequestParam("bookId") Integer id, Model model) {
        Optional<Book> optionalBook = bookService.getById(id);

        if(optionalBook.isPresent() && optionalBook.get().getAvailableCopies() > 0) {
            Book book = optionalBook.get();
            book.setAvailableCopies(book.getAvailableCopies() - 1);

            Subscription subscription = new Subscription();
            subscription.setUser(userService.findById(1).get());
            subscription.setBook(book);
            subscription.setStartDate(String.valueOf(new Date()));
            subscription.setApproved(false);
            subscription.setPeriod(60);

            subscriptionService.save(subscription);
            bookService.save(book);

            String message = "You have successfully ordered " + book.getName() + ".";
            model.addAttribute("message", message);

            return "order";
        } else {
            return "redirect:/error";
        }
    }
}