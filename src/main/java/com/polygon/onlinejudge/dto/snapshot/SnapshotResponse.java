package com.polygon.onlinejudge.dto.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotResponse {
    private UUID snapshotId;
    private UUID problemId;
    private UUID versionId;
    private Integer versionNumber;
    private Boolean isCommitted;
}
