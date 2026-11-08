package com.example.gamerspawn.client;

import com.example.gamerspawn.model.EventView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.List;
import java.util.Map;

@Component
public class BackendApiClient {

    private final RestClient restClient;

    public BackendApiClient(
            @Value("${backend.base-url}") String backendBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(backendBaseUrl)
                .build();
    }

    public String authenticate(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("user", username);
        form.add("encryptedPass", password);

        try {
            String token = restClient.post()
                    .uri("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);
            if (token == null || token.isBlank()) {
                throw new AuthenticationServiceException("No se pudo obtener un token de acceso.");
            }
            return token;
        } catch (RestClientResponseException exception) {
            throw new BadCredentialsException("No se pudieron validar las credenciales.", exception);
        } catch (ResourceAccessException exception) {
            throw new AuthenticationServiceException("No se pudo conectar con el servicio de autenticación.", exception);
        }
    }

    public List<EventView> getEvents() {
        return restClient.get()
                .uri("/api/events")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

        public List<EventView> searchEvents(Map<String, String> filters) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/events/search");
                    Map<String, String> variables = new java.util.HashMap<>();
                    filters.forEach((name, value) -> {
                        if (value != null && !value.isBlank()) {
                            uriBuilder.queryParam(name, "{" + name + "}");
                            variables.put(name, value);
                        }
                    });
                    return uriBuilder.build(variables);
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<EventView> getPrivateEvents(String token) {
        return restClient.get()
                .uri("/api/private/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public EventView getEventById(Long id, String token) {
        return restClient.get()
                .uri("/api/private/events/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(EventView.class);
    }
}