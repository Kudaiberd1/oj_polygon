package com.polygon.onlinejudge.dto.judge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Judge0SubmissionRequest {
    private String source_code;
    private Integer language_id;
    private String stdin;
    private Double cpu_time_limit;
    private Double wall_time_limit;
    private Long memory_limit;
}