package com.example.gamerspawn.controller;

import com.example.gamerspawn.auth.TokenStore;
import com.example.gamerspawn.client.BackendApiClient;
import com.example.gamerspawn.model.EventView;
import com.example.gamerspawn.model.CommentView;
import com.example.gamerspawn.model.MediaView;
import com.example.gamerspawn.model.RatingView;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class EventInfoController {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;
    private final String publicUrl;

    public EventInfoController(
            BackendApiClient backendApiClient,
            TokenStore tokenStore,
            @Value("${app.public-url}") String publicUrl) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
        this.publicUrl = publicUrl;
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
            Authentication authentication,
            Model model) {
        String token = tokenStore.requireToken(session);
        EventView event;
        try {
            event = backendApiClient.getEventById(id, token);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Evento no encontrado.",
                    exception);
        }

        List<CommentView> comments = List.of();
        RatingView rating = null;
        try {
            comments = backendApiClient.getComments(id, token);
            rating = backendApiClient.getRating(id, token);
        } catch (RestClientException exception) {
            model.addAttribute(
                    "feedbackError",
                    "No se pudieron cargar los comentarios y valoraciones.");
        }

        List<MediaView> mediaItems = List.of();
        try {
            mediaItems = backendApiClient.getMedia(id, token);
        } catch (RestClientException exception) {
            model.addAttribute(
                    "mediaError",
                    "No se pudieron cargar las fotos y videos.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        model.addAttribute("event", event);
        model.addAttribute("comments", comments);
        model.addAttribute("rating", rating);
        model.addAttribute("mediaItems", mediaItems);
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("shareUrl", publicUrl + "/event-info/" + id);
        return "event-info";
    }
}
