package com.example.gamerspawn.controller;

import com.example.gamerspawn.client.BackendApiClient;
import com.example.gamerspawn.model.EventView;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

@Controller
public class EventSearchController {

    private static final int MAX_FILTER_LENGTH = 100;

    private final BackendApiClient backendApiClient;

    public EventSearchController(BackendApiClient backendApiClient) {
        this.backendApiClient = backendApiClient;
    }

    @GetMapping("/events")
    public String searchEvents(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String videoGame,
            @RequestParam(required = false, defaultValue = "") String eventType,
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String country,
            @RequestParam(required = false, defaultValue = "") String city,
            @RequestParam(required = false, defaultValue = "") String modality,
            @RequestParam(required = false, defaultValue = "") String eventDate,
            Model model) {

        String errorMessage = null;

        String cleanDate = clean(eventDate);
        if (!cleanDate.isEmpty()) {
            try {
                LocalDate.parse(cleanDate);
            } catch (DateTimeParseException exception) {
                cleanDate = "";
                errorMessage = "La fecha ingresada no es válida. Se muestran los eventos sin filtrar por fecha.";
            }
        }

        Map<String, String> filters = new LinkedHashMap<>();
        filters.put("title", clean(title));
        filters.put("videoGame", clean(videoGame));
        filters.put("eventType", clean(eventType));
        filters.put("category", clean(category));
        filters.put("country", clean(country));
        filters.put("city", clean(city));
        filters.put("modality", clean(modality));
        filters.put("eventDate", cleanDate);

        List<EventView> events;
        try {
            events = backendApiClient.searchEvents(filters);
        } catch (RestClientException exception) {
            events = List.of();
            errorMessage = "No fue posible consultar los eventos en este momento. Intenta nuevamente más tarde.";
        }

        model.addAttribute("events", events);
        model.addAttribute("filters", filters);
        model.addAttribute("errorMessage", errorMessage);
        return "events";
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() > MAX_FILTER_LENGTH
                ? trimmed.substring(0, MAX_FILTER_LENGTH)
                : trimmed;
    }
}