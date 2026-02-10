package com.polygon.onlinejudge.dto.problemVersion;

import com.polygon.onlinejudge.entities.enums.ContestType;
import com.polygon.onlinejudge.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemVersionResponse {
    private UUID id;
    private UUID problemId;
    private int version;
    private Status status;
    private ProblemStatementResponse statement;
    private Long timeLimitMs;
    private Long memoryLimitMb;
    private ContestType scoringType;
    private LocalDateTime createdAt;
}
