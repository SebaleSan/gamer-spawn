package com.example.gamerspawn.security;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Base64;
import java.util.List;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.gamerspawn.auth.TokenStore;
import com.example.gamerspawn.client.BackendApiClient;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomAuthenticationProvider(
            BackendApiClient backendApiClient,
            TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Object credentials = authentication.getCredentials();
        if (username == null || username.isBlank() || credentials == null) {
            return null;
        }

        String token = backendApiClient.authenticate(username, credentials.toString());
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new AuthenticationServiceException("No se encontró la sesión de la solicitud de acceso.");
        }

        HttpServletRequest request = requestAttributes.getRequest();
        tokenStore.save(request.getSession(true), token);
        return UsernamePasswordAuthenticationToken.authenticated(
                username,
                null,
                authoritiesFromToken(token));
    }

    private Collection<SimpleGrantedAuthority> authoritiesFromToken(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                throw new AuthenticationServiceException("El token JWT tiene un formato inválido.");
            }

            String payload = new String(
                    Base64.getUrlDecoder().decode(tokenParts[1]),
                    StandardCharsets.UTF_8);
            JsonNode authorities = objectMapper.readTree(payload).get("authorities");

            if (authorities == null || !authorities.isArray()) {
                throw new AuthenticationServiceException("El token JWT no contiene roles válidos.");
            }

            List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (JsonNode authority : authorities) {
                if (authority.isTextual() && !authority.asText().isBlank()) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(authority.asText()));
                }
            }

            if (grantedAuthorities.isEmpty()) {
                throw new AuthenticationServiceException("El token JWT no contiene roles válidos.");
            }
            return grantedAuthorities;
        } catch (IllegalArgumentException | JsonProcessingException exception) {
            throw new AuthenticationServiceException("No se pudieron leer los roles del token JWT.", exception);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}