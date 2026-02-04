package com.polygon.onlinejudge.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestGroupResponse {
    private UUID id;
    private String problemVersionId;
    private List<TestCaseResponse> tests;
    private int points;
}
