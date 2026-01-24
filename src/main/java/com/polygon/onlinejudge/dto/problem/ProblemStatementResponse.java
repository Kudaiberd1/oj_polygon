package com.polygon.onlinejudge.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemStatementResponse {
    private UUID id;
    private String problemVersionId;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String notes;
}
