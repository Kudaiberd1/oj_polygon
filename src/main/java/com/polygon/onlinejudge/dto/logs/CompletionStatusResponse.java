package com.polygon.onlinejudge.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompletionStatusResponse {
    private CompletionStatusItem statement;
    private CompletionStatusItem tests;
    private CompletionStatusItem solution;
    private CompletionStatusItem validation;
}
