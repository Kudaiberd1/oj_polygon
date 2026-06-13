package com.polygon.onlinejudge.dto.generator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ScriptRunRequest {
    @NotNull
    private UUID testGroupId;

    @NotBlank
    private String script;
}
