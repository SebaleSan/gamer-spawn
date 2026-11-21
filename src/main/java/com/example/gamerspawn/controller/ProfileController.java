package com.example.gamerspawn.controller;

import com.example.gamerspawn.auth.TokenStore;
import com.example.gamerspawn.client.BackendApiClient;
import com.example.gamerspawn.model.EventView;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final List<String> EVENT_FIELDS = List.of(
            "title",
            "eventDate",
            "eventTime",
            "location",
            "modality",
            "eventType",
            "videoGame",
            "category",
            "country",
            "city",
            "description",
            "organizer",
            "participationRequirements",
            "registrationInformation",
            "accessLink",
            "imageUrl");

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;

    public ProfileController(BackendApiClient backendApiClient, TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        String token = tokenStore.requireToken(session);
        List<EventView> myEvents = List.of();
        try {
            myEvents = backendApiClient.getMyEvents(token);
        } catch (RestClientException exception) {
            model.addAttribute(
                    "profileError",
                    "No se pudieron cargar tus eventos.");
        }

        model.addAttribute("myEvents", myEvents);
        model.addAttribute("today", LocalDate.now());
        if (!model.containsAttribute("eventForm")) {
            model.addAttribute("eventForm", Map.of());
        }
        return "profile";
    }

    @PostMapping("/events")
    public String createEvent(
            @RequestParam Map<String, String> params,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Map<String, String> eventForm = new LinkedHashMap<>();
        Map<String, Object> event = new HashMap<>();
        for (String field : EVENT_FIELDS) {
            String value = params.getOrDefault(field, "").trim();
            eventForm.put(field, value);
            event.put(field, value.isEmpty() ? null : value);
        }

        try {
            backendApiClient.createEvent(event, tokenStore.requireToken(session));
            redirectAttributes.addFlashAttribute(
                    "profileMessage",
                    "Evento creado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "No se pudo crear el evento. Revisa los campos obligatorios, que la fecha no sea anterior a hoy y que las URLs comiencen con https://.");
            redirectAttributes.addFlashAttribute("eventForm", eventForm);
        }
        return "redirect:/profile";
    }
}
