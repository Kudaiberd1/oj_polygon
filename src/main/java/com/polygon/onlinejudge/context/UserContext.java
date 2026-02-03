package com.polygon.onlinejudge.context;

import com.polygon.onlinejudge.entities.User;
import com.polygon.onlinejudge.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class UserContext {
    private final UserRepository userRepository;
    private User cachedUser;

    public User getUser(String email) {
        if (cachedUser == null) {
            cachedUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        return cachedUser;
    }
}
