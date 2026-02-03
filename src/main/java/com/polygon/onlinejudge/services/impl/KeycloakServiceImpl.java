package com.polygon.onlinejudge.services.impl;


import com.polygon.onlinejudge.dto.keycloak.AuthResponse;
import com.polygon.onlinejudge.dto.keycloak.KeycloakTokenResponse;
import com.polygon.onlinejudge.dto.keycloak.RegisterRequest;
import com.polygon.onlinejudge.entities.User;
import com.polygon.onlinejudge.exceptions.UnauthorizedException;
import com.polygon.onlinejudge.repositories.UserRepository;
import com.polygon.onlinejudge.services.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${spring.application.jwt.keycloak.url}")
    private String keycloakUrl;
    @Value("${spring.application.jwt.keycloak.client-id}")
    private String clientId;
    @Value("${spring.application.jwt.keycloak.client-secret}")
    private String clientSecret;

    @Override
    public AuthResponse getAuthResponse(String email, String password) {
        log.info("username={}", email);
        if(email.isEmpty() || password.isEmpty()){
            throw new IllegalArgumentException("Username or password is null or blank");
        }
        String tokenUrl = buildTokenEndpoint("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", email);
        form.add("email", email);
        form.add("password", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, KeycloakTokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                return mapTokenResponse(response.getBody());
            } else {
                throw new IllegalArgumentException("Failed to get AuthResponse");
            }
        }catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Authentication failed for user '{}': Invalid credentials", email);
            throw new UnauthorizedException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            log.error("HTTP error from Keycloak: {} - {}", e.getStatusCode(), e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException("Invalid credentials");
            }
            throw new UnauthorizedException("Invalid credentials");
        } catch (Exception e) {
            log.error("Unexpected error during authentication: ", e);
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    @Override
    public void registerUser(RegisterRequest request) {
        String adminToken = getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = Map.of(
                "username", request.getEmail(),
                "email", request.getEmail(),
                "firstName", request.getEmail(),
                "lastName", request.getEmail(),
                "enabled", true
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(user, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    "http://localhost:8080/admin/realms/online_judge/users",
                    entity,
                    Void.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {


                String location = response.getHeaders().getLocation().toString();
                String userId = location.substring(location.lastIndexOf("/") + 1);

                try {
                    setPassword(userId, request.getPassword(), adminToken);

                    User newUser = new User();
                    newUser.setEmail(request.getEmail());
                    userRepository.save(newUser);
                } catch (DataAccessException dbEx) {
                    log.error("DB save failed after Keycloak user creation. Rolling back Keycloak userId={}", userId, dbEx);
                    try {
                        deleteKeycloakUser(userId, adminToken);
                    } catch (Exception rollbackEx) {
                        log.error("Failed to rollback Keycloak userId={} (zombie risk). Manual cleanup may be needed.", userId, rollbackEx);
                    }
                    throw dbEx;
                } catch (Exception ex) {
                    log.error("Registration failed after Keycloak user creation. Rolling back Keycloak userId={}", userId, ex);
                    try {
                        deleteKeycloakUser(userId, adminToken);
                    } catch (Exception rollbackEx) {
                        log.error("Failed to rollback Keycloak userId={} (zombie risk). Manual cleanup may be needed.", userId, rollbackEx);
                    }
                    throw ex;
                }
            }else{
                throw new BadRequestException("Unexpected error during registration");
            }
        }catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Invalid credentials");
            throw new UnauthorizedException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            log.error("HTTP error from Keycloak: {} - {}", e.getStatusCode(), e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException("Invalid credentials");
            }
            throw new UnauthorizedException("Invalid credentials");
        } catch (Exception e) {
            log.error("Unexpected error during registration: ", e);
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    @Override
    public @Nullable AuthResponse refreshToken(String refreshToken) {
        log.info("Refresh attempt");
        String tokenUrl = buildTokenEndpoint("token");
        if(refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is empty");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, KeycloakTokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {

                if (response.getBody() != null) {
                    return mapTokenResponse(response.getBody());
                } else {
                    throw new RuntimeException("Unexpected Refresh Response!");
                }
            }
            throw new RuntimeException("Unexpected Refresh Response!");
        }catch (HttpClientErrorException e){
            String msg = e.getResponseBodyAsString();
            log.warn("Token refresh failed with status {}: {}", e.getStatusCode(), msg);
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new UnauthorizedException("Invalid grant!");
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException("Invalid ClientId!");
            }
            throw new UnauthorizedException("Refresh failed!");
        }catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            throw new UnauthorizedException("Refresh failed!");
        }
    }

    public void setPassword(String userId, String password, String adminToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.put(
                "http://localhost:8080/admin/realms/online_judge/users/" + userId + "/reset-password",
                request
        );
    }

    public String getAdminToken(){

        String url = buildTokenEndpoint("token");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public void deleteKeycloakUser(String userId, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String deleteUrl = "http://localhost:8080/admin/realms/online_judge/users/"+userId;

        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, request, Void.class);
    }


    public String buildTokenEndpoint(String endpointType){
        String base = keycloakUrl;
        return base + "/protocol/openid-connect/" + endpointType;
    }

    public AuthResponse mapTokenResponse(KeycloakTokenResponse tokenResponse) {
        return AuthResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .expiresIn(tokenResponse.getExpiresIn())
                .tokenType(tokenResponse.getTokenType())
                .build();
    }
}
