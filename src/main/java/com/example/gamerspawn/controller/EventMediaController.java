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
@RequestMapping("/event-info/{id}/media")
public class EventMediaController {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;

    public EventMediaController(BackendApiClient backendApiClient, TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @PostMapping
    public String addMedia(
            @PathVariable Long id,
            @RequestParam("type") String type,
            @RequestParam("url") String url,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            backendApiClient.addMedia(
                    id,
                    type,
                    url,
                    tokenStore.requireToken(session));
            redirectAttributes.addFlashAttribute(
                    "mediaMessage",
                    "Contenido agregado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "mediaError",
                    "No se pudo agregar el contenido. Usa una URL https:// o un enlace de YouTube.");
        }
        return "redirect:/event-info/{id}#media";
    }

    @PostMapping("/{mediaId}/delete")
    public String deleteMedia(
            @PathVariable Long id,
            @PathVariable Long mediaId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            backendApiClient.deleteMedia(
                    id,
                    mediaId,
                    tokenStore.requireToken(session));
            redirectAttributes.addFlashAttribute(
                    "mediaMessage",
                    "Contenido eliminado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "mediaError",
                    "No se pudo eliminar el contenido.");
        }
        return "redirect:/event-info/{id}#media";
    }
}
