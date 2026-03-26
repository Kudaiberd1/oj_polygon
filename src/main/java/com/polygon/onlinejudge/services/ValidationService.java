package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.entities.ProblemVersion;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ValidationService {
    void verifyVersion(ProblemVersion problemVersion);
    void verifyAuthorSolution(ProblemVersion problemVersion);
    void setTestCaseOutput(Map<UUID, List<String>> testGroupOutputs);
}
