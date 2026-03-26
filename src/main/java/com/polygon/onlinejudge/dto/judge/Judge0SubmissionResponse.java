package com.polygon.onlinejudge.dto.judge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Judge0SubmissionResponse {

    private String stdout;
    private String time;
    private Integer memory;
    private String stderr;
    private String token;
    private String compile_output;
    private String message;
    private Status status;

    @Data
    public static class Status {
        private Integer id;
        private String description;
    }
}