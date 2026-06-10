package com.polygon.onlinejudge.dto.judge;

import com.polygon.onlinejudge.entities.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCodeRequest {

    @NotBlank
    private String sourceCode;

    @NotNull
    private Language language;

    private String stdin;
}
