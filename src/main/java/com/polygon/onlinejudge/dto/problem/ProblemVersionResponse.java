package com.polygon.onlinejudge.dto.problem;

import com.polygon.onlinejudge.entities.enums.ContestType;
import com.polygon.onlinejudge.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemVersionResponse {
    private String id;
    private String problemId;
    private int version;
    private Status status;
    private Long timeLimitMs;
    private Long memoryLimitMb;
    private ContestType scoringType;
    private LocalDateTime createdAt;
}
