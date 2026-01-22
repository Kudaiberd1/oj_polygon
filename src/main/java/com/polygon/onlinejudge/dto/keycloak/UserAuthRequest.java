package com.polygon.onlinejudge.dto.keycloak;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserAuthRequest(
        @NotBlank()
        @Email
        String email,

        @NotBlank(message = "{validation.auth.password.required}")
        @Size(max = 255, message = "{validation.auth.password.size}")
        String password
) {
}