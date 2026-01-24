package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.problem.ProblemStatementRequest;
import com.polygon.onlinejudge.dto.problem.ProblemStatementResponse;
import com.polygon.onlinejudge.dto.problem.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problem.ProblemVersionResponse;

import java.util.UUID;

public interface ProblemVersionService {

    ProblemVersionResponse createVersion(UUID problemId, ProblemVersionRequest request);

    ProblemVersionResponse getVersion(UUID versionId);

    ProblemStatementResponse updateStatement(UUID versionId, ProblemStatementRequest request);

    ProblemStatementResponse getStatement(UUID versionId);

    ProblemVersionResponse updateVersion(UUID versionId, ProblemVersionRequest request);

    void finalizeVersion(UUID versionId);
}
