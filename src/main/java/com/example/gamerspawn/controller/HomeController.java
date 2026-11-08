package com.example.gamerspawn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gamerspawn.client.BackendApiClient;
import com.example.gamerspawn.model.EventView;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.List;

@Controller
public class HomeController {

    private final BackendApiClient backendApiClient;

    public HomeController(BackendApiClient backendApiClient) {
        this.backendApiClient = backendApiClient;
    }

    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {
        List<EventView> events = backendApiClient.getEvents();
        model.addAttribute("events", events);
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("username", authentication.getName());
        }
        return "home";
    }

    
}
