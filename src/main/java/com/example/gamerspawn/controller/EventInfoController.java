package com.example.gamerspawn.controller;

import com.example.gamerspawn.auth.TokenStore;
import com.example.gamerspawn.client.BackendApiClient;
import com.example.gamerspawn.model.EventView;
import com.example.gamerspawn.model.CommentView;
import com.example.gamerspawn.model.RatingView;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        model.addAttribute("event", event);
        model.addAttribute("comments", comments);
        model.addAttribute("rating", rating);
        model.addAttribute("currentUsername", authentication.getName());
        return "event-info";
    }

    @PostMapping("/event-info/{id}/comments")
    public String createComment(
            @PathVariable Long id,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            backendApiClient.createComment(
                    id,
                    content,
                    tokenStore.requireToken(session));
            redirectAttributes.addFlashAttribute(
                    "feedbackMessage",
                    "Comentario publicado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "feedbackError",
                    "No se pudo publicar el comentario.");
        }
        return "redirect:/event-info/{id}#comments";
    }

    @PostMapping("/event-info/{id}/comments/{commentId}/edit")
    public String updateComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            backendApiClient.updateComment(
                    id,
                    commentId,
                    content,
                    tokenStore.requireToken(session));
            redirectAttributes.addFlashAttribute(
                    "feedbackMessage",
                    "Comentario actualizado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "feedbackError",
                    "No se pudo actualizar el comentario.");
        }
        return "redirect:/event-info/{id}#comments";
    }

    @PostMapping("/event-info/{id}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            backendApiClient.deleteComment(
                    id,
                    commentId,
                    tokenStore.requireToken(session));
            redirectAttributes.addFlashAttribute(
                    "feedbackMessage",
                    "Comentario eliminado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "feedbackError",
                    "No se pudo eliminar el comentario.");
        }
        return "redirect:/event-info/{id}#comments";
    }

    @PostMapping("/event-info/{id}/rating")
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

    @PostMapping("/event-info/{id}/rating/delete")
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
