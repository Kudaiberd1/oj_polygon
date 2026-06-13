package com.polygon.onlinejudge.dto.generator;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GeneratorScriptResponse {
    private UUID id;
    private UUID versionId;
    private String content;
    private LocalDateTime updatedAt;
}
