package com.fundoo.notes.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }
}
