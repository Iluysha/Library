package com.epam.library.controller;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.service.BookService;
import com.epam.library.service.SubscriptionService;
import com.epam.library.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class SubscriptionController {

    private static final Logger log = LogManager.getLogger(BookController.class);
    private final UserService userService;
    private final BookService bookService;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(UserService userService, BookService bookService,
                                  SubscriptionService subscriptionService) {
        this.userService = userService;
        this.bookService = bookService;
        this.subscriptionService = subscriptionService;
    }

    /*
        Method, handling get request to the /subscriptions URL.
    */
    @GetMapping("/subscriptions")
    public String subscriptions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Handling subscriptions request for user: {}", userDetails.getUsername());

        if(userDetails.getAuthorities().stream().anyMatch(authority ->
                authority.getAuthority().equals("ROLE_LIBRARIAN"))) {
            model.addAttribute("subscriptions",
                    subscriptionService.findAll());
        } else {
            model.addAttribute("subscriptions",
                    subscriptionService.findByUserEmail(userDetails.getUsername()));
        }

        return "subscriptions";
    }

    @GetMapping("/orders")
    public String orders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Handling orders request for user: {}", userDetails.getUsername());

        model.addAttribute("orders",
                subscriptionService.findByUserEmail(userDetails.getUsername()));

        return "orders";
    }

    @GetMapping("/order")
    public String orderSuccess() {
        log.info("Handling order success request");

        return "order";
    }

    /*
    Method, handling get request to the /order URL after user press ´Order´ button.
    Gets the id of the book from the request, checks if this book exists in the library
    and there are available copies. Then reduce number of available copies by one and saves the book.
    Then returns order page with the message about the order.
    If there is no such a book in the library or no available copies, redirects to the error page.
    */
    @PostMapping("/order")
    public String orderBook(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("bookId") Integer id,
                            RedirectAttributes attributes) {
        log.info("Processing order request for user: {} and book id: {}", userDetails.getUsername(), id);

        Optional<Book> optionalBook = bookService.getById(id);

        if(optionalBook.isPresent() && optionalBook.get().getAvailableCopies() > 0) {
            Book book = optionalBook.get();
            book.setAvailableCopies(book.getAvailableCopies() - 1);

            Subscription subscription = new Subscription();
            subscription.setUser(userService.findByEmail(userDetails.getUsername()).orElseThrow(() ->
                    new UsernameNotFoundException("User not found")));
            subscription.setBook(book);
            subscription.setStartDate(LocalDate.now());
            subscription.setApproved(false);
            subscription.setPeriod(60);

            subscriptionService.save(subscription);
            bookService.save(book);

            log.info("Order placed successfully for user: {} and book id: {}", userDetails.getUsername(), id);

            return "redirect:order";
        } else {
            log.warn("Failed to place order for user: {} and book id: {}. No such book or no available copies.",
                    userDetails.getUsername(), id);

            attributes.addFlashAttribute("msg_code", "no_such_book");
            return "redirect:/error";
        }
    }
}