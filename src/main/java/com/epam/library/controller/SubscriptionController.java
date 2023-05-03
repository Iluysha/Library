package com.epam.library.controller;

import com.epam.library.service.SubscriptionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubscriptionController {

    private final SubscriptionService subscriptionService;


    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /*
        Method, handling get request to the /subscriptions URL.
    */
    @GetMapping("/subscriptions")
    public String subscriptions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
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
        model.addAttribute("orders",
                subscriptionService.findByUserEmail(userDetails.getUsername()));

        return "orders";
    }
}
