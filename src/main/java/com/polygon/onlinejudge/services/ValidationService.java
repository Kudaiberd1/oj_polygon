package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.entities.ProblemVersion;

import java.util.Map;

public interface ValidationService {
    void verifyVersion(ProblemVersion problemVersion);
    void verifyAuthorSolution(ProblemVersion problemVersion);
    void clearTestCaseOutputs(ProblemVersion problemVersion);
    void setTestCaseOutput(Map<Long, String> testOutputs);
}
