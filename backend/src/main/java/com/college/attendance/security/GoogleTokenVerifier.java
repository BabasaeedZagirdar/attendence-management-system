package com.college.attendance.security;

import com.college.attendance.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/** Verifies a Google ID token (from Google Identity Services on the frontend) via Google's tokeninfo endpoint. */
@Component
public class GoogleTokenVerifier {
    public record GoogleUser(String email, String name) {}

    @Value("${app.google.client-id:}")
    private String clientId;

    @SuppressWarnings("unchecked")
    public GoogleUser verify(String idToken) {
        if (clientId == null || clientId.isBlank())
            throw ApiException.bad("Google login is not configured (set GOOGLE_CLIENT_ID)");
        Map<String, Object> body;
        try {
            body = RestClient.create().get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={t}", idToken)
                    .retrieve().body(Map.class);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Google token");
        }
        if (body == null || !clientId.equals(body.get("aud")) || !"true".equals(String.valueOf(body.get("email_verified"))))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Google token rejected");
        String email = String.valueOf(body.get("email"));
        String name = body.get("name") == null ? email : String.valueOf(body.get("name"));
        return new GoogleUser(email.toLowerCase(), name);
    }
}
