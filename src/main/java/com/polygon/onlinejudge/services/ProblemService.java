package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.pagination.PaginationParams;
import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.dto.problem.ProblemVersionResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProblemService {

    Page<ProblemResponse> getAllProblems(String email, PaginationParams paginationParams);

    ProblemResponse getById(UUID id, String email);

    ProblemResponse createProblem(String email, ProblemRequest problemRequest);

    List<ProblemVersionResponse> getProblemVersions(UUID problemId);
}
