package com.polygon.onlinejudge.dto.test;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExampleTestCaseRequest {
    private String customOutput;
}
