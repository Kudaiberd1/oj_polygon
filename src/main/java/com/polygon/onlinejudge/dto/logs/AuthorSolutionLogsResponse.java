package com.polygon.onlinejudge.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorSolutionLogsResponse {
    private Long testCaseId;
    private Long orderId;
    private UUID groupId;
    private String status;
    private String time;
    private Long memory;
    private String message;
    private String log;
}
