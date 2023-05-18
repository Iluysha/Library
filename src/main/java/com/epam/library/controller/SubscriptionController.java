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

/**
 * Controller class for managing subscription-related operations.
 */
@Controller
public class SubscriptionController {

    private static final Logger log = LogManager.getLogger(SubscriptionController.class);
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Handles the GET request to the /subscriptions URL.
     * Retrieves the subscriptions for the logged-in user and returns the appropriate view.
     * If the user is a librarian or admin, returns the "librarian_subscriptions" view with all subscriptions.
     * If the user is a reader, returns the "reader_subscriptions" view with their subscriptions.
     *
     * @param userDetails The UserDetails of the logged-in user.
     * @param model        The Model object for passing data to the view.
     * @return The view name for rendering the subscriptions page.
     */
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

    /**
     * Handles the GET request to the /order URL.
     * Returns the "order" page for displaying a successful order.
     *
     * @return The view name for rendering the "order" page.
     */
    @GetMapping("/order")
    public String orderSuccess() {
        log.info("Handling order success request");
        return "order";
    }

    /**
     * Handles the POST request to the /order URL.
     * Orders a book with the specified book ID for the logged-in user.
     * If the book is ordered successfully, redirects to the "order" page.
     * Otherwise, redirects to the "error" page with a flash attribute indicating the failure reason.
     *
     * @param userDetails The UserDetails of the logged-in user.
     * @param id           The ID of the book to order.
     * @param attributes   RedirectAttributes for adding flash attributes.
     * @return The view name for redirection.
     */
    @PostMapping("/order")
    public String orderBook(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("bookId") Integer id,
                            RedirectAttributes attributes) {
        try {
            subscriptionService.orderBook(userDetails, id);
            return "redirect:order";
        } catch (Exception e) {
            attributes.addFlashAttribute("msg_code", "no_such_book");
            return "redirect:error";
        }
    }

    /**
     * Handles the POST request to the /approve URL.
     * Approves a subscription with the specified subscription ID.
     * If the subscription is approved successfully, redirects to the "subscriptions" page.
     * Otherwise, redirects to the "error" page with a flash attribute indicating the failure reason.
     *
     * @param id          The ID of the subscription to approve.
     * @param attributes  RedirectAttributes for adding flash attributes.
     * @return The view name for redirection.
     */
    @PostMapping("/approve")
    public String approveSubscription(@RequestParam("subscriptionId") Integer id,
                                      RedirectAttributes attributes) {
        log.info("Processing approve request for subscription id: {}", id);

        try {
            subscriptionService.approveSubscription(id);
            return "redirect:subscriptions";
        } catch (Exception e) {
            log.warn("Failed to approve subscription with id:  {}", id);
            attributes.addFlashAttribute("msg_code", "cant_approve");
            return "redirect:error";
        }
    }
}