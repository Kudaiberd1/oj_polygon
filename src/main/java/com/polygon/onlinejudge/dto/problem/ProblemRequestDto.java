package com.polygon.onlinejudge.dto.problem;

import com.polygon.onlinejudge.entities.enums.ContestType;
import com.polygon.onlinejudge.entities.enums.Language;
import com.polygon.onlinejudge.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemRequestDto {

    private String title;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private Long timeLimitMs;
    private Long memoryLimitMb;
    private Language allowedLanguages;
    private ContestType scoringType;
    private UUID ownerId;
    private Status status;
}
