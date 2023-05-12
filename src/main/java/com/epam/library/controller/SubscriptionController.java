package com.epam.library.controller;

import com.epam.library.service.SubscriptionService;
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

@Controller
public class SubscriptionController {

    private static final Logger log = LogManager.getLogger(SubscriptionController.class);
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/subscriptions")
    public String subscriptions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Handling subscriptions request for user: {}", userDetails.getUsername());

        if(userDetails.getAuthorities().stream().anyMatch(authority ->
                authority.getAuthority().equals("ROLE_LIBRARIAN") ||
                        authority.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("subscriptions",
                    subscriptionService.findAll());
            return "librarian_subscriptions";
        } else {
            model.addAttribute("subscriptions",
                    subscriptionService.findByUserEmail(userDetails.getUsername()));
            return "reader_subscriptions";
        }
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
        if(subscriptionService.orderBook(userDetails, id) != null) {
            return "redirect:order";
        } else {
            log.warn("No book with id: {}", id);
            attributes.addFlashAttribute("msg_code", "no_such_book");
            return "redirect:error";
        }
    }

    @PostMapping("/approve")
    public String approveSubscription(@RequestParam("subscriptionId") Integer id,
                                      RedirectAttributes attributes) {
        log.info("Processing approve request for subscription id: {}", id);

        if(subscriptionService.approveSubscription(id) != null) {
            return "redirect:subscriptions";
        } else {
            log.warn("Failed to approve subscription with id:  {}", id);
            attributes.addFlashAttribute("msg_code", "cant_approve");
            return "redirect:error";
        }
    }
}