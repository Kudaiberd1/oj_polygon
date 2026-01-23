package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.keycloak.AuthResponse;
import com.polygon.onlinejudge.dto.keycloak.RegisterRequest;
import com.polygon.onlinejudge.dto.keycloak.UserAuthRequest;
import com.polygon.onlinejudge.services.KeycloakService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon/auth")
public class AuthController {

    private final KeycloakService keycloakService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid UserAuthRequest userAuthRequest){
        AuthResponse authResponse = keycloakService.getAuthResponse(userAuthRequest.email(), userAuthRequest.password());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegisterRequest registserRequest){
        keycloakService.registerUser(registserRequest);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam("token") String token){
        return ResponseEntity.ok(keycloakService.refreshToken(token));
    }
}
