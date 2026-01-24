package com.polygon.onlinejudge.policy;

import com.polygon.onlinejudge.entities.Problem;
import com.polygon.onlinejudge.entities.User;
import com.polygon.onlinejudge.exceptions.ForbiddenException;
import com.polygon.onlinejudge.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemPolicy {
    private final UserRepository userRepository;

    public void checkIsOwner(Problem problem, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!problem.getOwnerId().equals(user.getId())) {
            throw new ForbiddenException("You are not the author of this problem");
        }
    }
}
