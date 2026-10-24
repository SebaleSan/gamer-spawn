package com.example.gamerspawn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController {

    @GetMapping("/event-info")
    public String eventInfo() {
        return "event-info";
    }
}
