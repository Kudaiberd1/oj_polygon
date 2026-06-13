package com.polygon.onlinejudge.dto.generator;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GeneratorResponse {
    private UUID id;
    private UUID versionId;
    private String name;
    private String sourceCodeKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
