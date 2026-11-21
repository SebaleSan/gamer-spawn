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
@RequestMapping("/event-info/{id}/comments")
public class EventCommentController {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;

    public EventCommentController(BackendApiClient backendApiClient, TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @PostMapping
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

    @PostMapping("/{commentId}/edit")
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

    @PostMapping("/{commentId}/delete")
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
}
