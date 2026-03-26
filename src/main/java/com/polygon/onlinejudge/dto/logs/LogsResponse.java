package com.polygon.onlinejudge.dto.logs;

import com.polygon.onlinejudge.entities.ProblemVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogsResponse {
    private UUID id;
    private UUID problemId;
    private UUID versionId;
    private String status;
    private String log;
    private String message;
    private String time;
    private Long memory;
    private LocalDateTime verifiedAt;
}
