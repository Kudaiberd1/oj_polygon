package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.dto.problem.ProblemVersionResponse;

import java.util.List;
import java.util.UUID;

public interface ProblemService {

    List<ProblemResponse> getAllProblems(String email);

    ProblemResponse getById(UUID id, String email);

    ProblemResponse createProblem(String email, ProblemRequest problemRequest);

    List<ProblemVersionResponse> getProblemVersions(UUID problemId);
}
