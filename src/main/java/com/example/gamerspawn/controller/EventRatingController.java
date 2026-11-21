package com.example.gamerspawn.controller;

import com.example.gamerspawn.auth.TokenStore;
import com.example.gamerspawn.client.BackendApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/event-info/{id}/rating")
public class EventRatingController {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;

    public EventRatingController(BackendApiClient backendApiClient, TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @PostMapping
    public String saveRating(
            @PathVariable Long id,
            @RequestParam("score") Integer score,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            backendApiClient.saveRating(
                    id,
                    score,
                    tokenStore.requireToken(session));
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "ratingError",
                    "No se pudo guardar la valoración.");
        }
        return "redirect:/event-info/{id}";
    }

    @PostMapping("/delete")
    public String deleteRating(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            backendApiClient.deleteRating(
                    id,
                    tokenStore.requireToken(session));
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "ratingError",
                    "No se pudo eliminar la valoración.");
        }
        return "redirect:/event-info/{id}";
    }
}
