package com.epam.library.controller;

import com.epam.library.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "redirect:books";
    }

    @GetMapping("/account")
    public String account(Model model) {
        User user = new User();
        user.setName("Illia Mykhailichenko");
        user.setEmail("ilua.mix12@gmail.com");
        user.setPassword("1204");
        user.setFine(0);

        model.addAttribute("user", user);

        return "account";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
