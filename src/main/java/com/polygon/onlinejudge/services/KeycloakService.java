package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.keycloak.AuthResponse;
import com.polygon.onlinejudge.dto.keycloak.RegisterRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public interface KeycloakService {
    AuthResponse getAuthResponse(String email, String password);

    void registerUser(RegisterRequest request);

    @Nullable AuthResponse refreshToken(String refreshToken);
}
