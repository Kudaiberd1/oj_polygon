package com.polygon.onlinejudge.dto.problem;

import com.polygon.onlinejudge.entities.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorSolutionRequest {
    private Language language;
    private String sourceCode;
}
