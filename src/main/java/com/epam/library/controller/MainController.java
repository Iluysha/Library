package com.epam.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String empty() {
        return "redirect:books";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
