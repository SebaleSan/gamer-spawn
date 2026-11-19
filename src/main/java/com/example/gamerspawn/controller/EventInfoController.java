package com.example.gamerspawn.controller;

import com.example.gamerspawn.auth.TokenStore;
import com.example.gamerspawn.client.BackendApiClient;
import com.example.gamerspawn.model.EventView;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EventInfoController {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;

    public EventInfoController(BackendApiClient backendApiClient, TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @GetMapping("/event-info")
    public String eventInfo(HttpSession session, Model model) {
        var events = backendApiClient.getPrivateEvents(tokenStore.requireToken(session));
        model.addAttribute("events", events);
        return "event-info";
    }

    @GetMapping("/event-info/{id}")
    public String eventInfoById(
            @PathVariable Long id,
            HttpSession session,
            Model model) {
        EventView event = backendApiClient.getEventById(
                id,
                tokenStore.requireToken(session));
        model.addAttribute("event", event);
        return "event-info";
    }
}
