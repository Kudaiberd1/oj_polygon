package com.polygon.onlinejudge.dto.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotResponse {
    private UUID snapshotId;
    private Integer sourceVersionNumber;
    private Long timeLimitMs;
    private Long memoryLimitMb;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isCommitted;
    private List<SnapshotTestCaseResponse> testCases;
}
