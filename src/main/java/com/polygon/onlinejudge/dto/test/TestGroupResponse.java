package com.polygon.onlinejudge.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestGroupResponse {
    private String id;
    private String problemVersionId;
    private int points;
}
