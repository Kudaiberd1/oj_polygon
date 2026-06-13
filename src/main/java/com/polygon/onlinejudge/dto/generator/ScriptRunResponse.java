package com.polygon.onlinejudge.dto.generator;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScriptRunResponse {
    private List<ScriptLineResult> results;
    private Integer totalLines;
    private Integer successCount;
    private Integer failCount;
}
