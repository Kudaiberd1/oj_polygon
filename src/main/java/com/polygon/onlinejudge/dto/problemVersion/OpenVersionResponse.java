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
public class OpenVersionResponse {
    private UUID versionId;
    private Integer versionNumber;
    private String status;
    private Boolean isReadOnly;
    private Boolean autoCreated;
}
