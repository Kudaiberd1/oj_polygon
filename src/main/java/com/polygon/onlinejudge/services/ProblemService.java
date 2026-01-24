package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;

import java.util.List;

public interface ProblemService {

    List<ProblemResponse> getAllproblems();

    ProblemResponse getById(String id);

    ProblemResponse createProblem(ProblemRequest problem);

    void updateProblem(ProblemRequest problem, String id);
}
