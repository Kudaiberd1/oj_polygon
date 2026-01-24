package com.polygon.onlinejudge.dto.problem;

import com.polygon.onlinejudge.entities.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorSolutionResponse {
    private UUID id;
    private String problemVersionId;
    private Language language;
    private String sourceCode;
}
