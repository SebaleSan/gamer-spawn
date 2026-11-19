package com.example.gamerspawn.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class TokenStore {

    private static final String TOKEN_SESSION_ATTRIBUTE = TokenStore.class.getName() + ".token";

    public void save(HttpSession session, String token) {
        session.setAttribute(TOKEN_SESSION_ATTRIBUTE, token);
    }

    public String requireToken(HttpSession session) {
        Object token = session.getAttribute(TOKEN_SESSION_ATTRIBUTE);
        if (token instanceof String jwt && !jwt.isBlank()) {
            return jwt;
        }
        throw new AuthenticationCredentialsNotFoundException("No hay un token válido para esta sesión.");
    }
}
