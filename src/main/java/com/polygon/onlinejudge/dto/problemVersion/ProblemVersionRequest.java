package com.polygon.onlinejudge.dto.problemVersion;

import com.polygon.onlinejudge.entities.enums.ContestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemVersionRequest {
    private Long timeLimitMs;
    private Long memoryLimitMb;
    private ContestType scoringType;
}
