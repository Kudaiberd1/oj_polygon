package com.polygon.onlinejudge.dto.generator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScriptLineResult {
    private String line;
    private boolean success;
    private String output;
    private String error;
    private Integer testOrderId;
}
