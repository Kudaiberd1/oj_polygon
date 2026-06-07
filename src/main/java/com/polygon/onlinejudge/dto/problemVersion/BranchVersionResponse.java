package com.polygon.onlinejudge.dto.problemVersion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchVersionResponse {
    private UUID newVersionId;
    private Integer newVersionNumber;
    private Integer branchedFromVersion;
    private String message;
}
