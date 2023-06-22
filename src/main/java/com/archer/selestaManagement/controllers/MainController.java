package com.archer.selestaManagement.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/home")
    public String home(Model model) {
        return "index";
    }
    @GetMapping("/welcome")
    public String greeting() {
        return "index";
    }

    @GetMapping("/import")
    public String importBOM() {
        return "import";
    }

    @GetMapping("/import-base")
    public String importBase() {
        return "importBase";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
