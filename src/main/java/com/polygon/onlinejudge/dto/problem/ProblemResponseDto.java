package com.polygon.onlinejudge.dto.problem;

import com.polygon.onlinejudge.entities.enums.ContestType;
import com.polygon.onlinejudge.entities.enums.Language;
import com.polygon.onlinejudge.entities.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemResponseDto {
    private String id;
    private String title;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private long timeLimitMs;
    private long memoryLimitMb;
    private Language allowedLanguages;
    private ContestType scoringType;
    private UUID ownerId;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
}
