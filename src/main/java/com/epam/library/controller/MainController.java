package com.epam.library.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);

    @GetMapping("/")
    public String empty() {
        log.info("Received request for empty URL, redirecting to books");
        return "redirect:books";
    }

    @GetMapping("/login")
    public String login() {
        log.info("Received request for login page");
        return "login";
    }
}
