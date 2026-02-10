package com.polygon.onlinejudge.dto.problemVersion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemStatementRequest {
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String notes;
}
