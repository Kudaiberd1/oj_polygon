package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.problem.AuthorSolutionRequest;
import com.polygon.onlinejudge.dto.problem.AuthorSolutionResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;

import java.util.UUID;

public interface ProblemVersionService {

    ProblemVersionResponse createVersion(UUID problemId, ProblemVersionRequest request);

    ProblemVersionResponse getVersion(UUID versionId);

    ProblemStatementResponse updateStatement(UUID versionId, ProblemStatementRequest request);

    ProblemStatementResponse getStatement(UUID versionId);

    ProblemVersionResponse updateVersion(UUID versionId, ProblemVersionRequest request);

    void finalizeVersion(UUID versionId);

    AuthorSolutionResponse addAuthorSolution(UUID versionId, AuthorSolutionRequest request);
}
