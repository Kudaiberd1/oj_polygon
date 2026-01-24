package com.polygon.onlinejudge.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemResponse {
    private String id;
    private String title;
    private UUID ownerId;
    private LocalDateTime createdAt;
}
