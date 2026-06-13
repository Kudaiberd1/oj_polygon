package com.polygon.onlinejudge.dto.generator;

import lombok.Data;

import java.util.UUID;

@Data
public class ScriptRunRequest {
    private UUID testGroupId;
    private String script;
}
