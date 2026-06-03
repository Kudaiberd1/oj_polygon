package com.polygon.onlinejudge.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompletionStatusItem {
    private boolean complete;
    private String detail;
}
