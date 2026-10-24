package com.example.gamerspawn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller 
public class HomeController {
    
    @GetMapping("/home")
    public String home(@RequestParam(name = "name", required = false, defaultValue = "Primera implemetancion de seguridad y calidad en el desarrollo de software") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }


    @GetMapping("/")
    public String root(@RequestParam (name = "name", required = false, defaultValue = "Root") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }
    
}
