package com.example.gamerspawn.controller;

import com.example.gamerspawn.client.BackendApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    private final BackendApiClient backendApiClient;

    public SignUpController(BackendApiClient backendApiClient) {
        this.backendApiClient = backendApiClient;
    }

    @GetMapping
    public String signUp() {
        return "sign-up";
    }

    @PostMapping
    public String register(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {
        try {
            backendApiClient.register(username, email, password);
            return "redirect:/login?registered";
        } catch (HttpClientErrorException.Conflict exception) {
            model.addAttribute("errorMessage", "El usuario o el correo ya están registrados.");
        } catch (HttpClientErrorException.BadRequest exception) {
            model.addAttribute(
                    "errorMessage",
                    "Revisa los datos: usuario de 3 a 30 caracteres (letras, números, punto, guion o guion bajo), correo válido y contraseña de al menos 8 caracteres.");
        } catch (RestClientException exception) {
            model.addAttribute("errorMessage", "No se pudo completar el registro. Intenta nuevamente más tarde.");
        }

        model.addAttribute("username", username);
        model.addAttribute("email", email);
        return "sign-up";
    }
}
