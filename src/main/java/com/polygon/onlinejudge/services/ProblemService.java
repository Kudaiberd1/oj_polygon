package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.problem.ProblemRequestDto;
import com.polygon.onlinejudge.dto.problem.ProblemResponseDto;

import java.util.List;

public interface ProblemService {

    List<ProblemResponseDto> getAllproblems();

    ProblemResponseDto getById(String id);

    ProblemResponseDto createProblem(ProblemRequestDto problem);

    void updateProblem(ProblemRequestDto problem, String id);
}
