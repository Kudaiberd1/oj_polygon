package com.polygon.onlinejudge.dto.problem;

import com.polygon.onlinejudge.entities.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorSolutionResponse {
    private String id;
    private String problemVersionId;
    private Language language;
    private String sourceCode;
}
