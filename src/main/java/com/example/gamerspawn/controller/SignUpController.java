package com.example.gamerspawn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class SignUpController {
    
    @GetMapping ("/sign-up")
    public String signUp() {
        return "sign-up";
    }
}
