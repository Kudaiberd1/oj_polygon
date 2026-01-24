package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.entities.Problem;
import com.polygon.onlinejudge.mappers.ProblemMapper;
import com.polygon.onlinejudge.repositories.ProblemRepository;
import com.polygon.onlinejudge.services.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;

    @Override
    public List<ProblemResponse> getAllproblems() {
        List<Problem> problems = problemRepository.findAll();
        return problems.stream().map(problemMapper::toDto).toList();
    }

    @Override
    public ProblemResponse getById(String id) {
        Problem problem = problemRepository.findById(id).orElseThrow();

        return problemMapper.toDto(problem);
    }

    @Override
    public ProblemResponse createProblem(ProblemRequest problem) {
        Problem newProblem = Problem.builder()
                .title(problem.getTitle())
                .ownerId(problem.getOwnerId())
                .build();

        problemRepository.save(newProblem);
        return problemMapper.toDto(newProblem);
    }

    @Override
    public void updateProblem(ProblemRequest request, String id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        problemMapper.updateProblem(request, problem);

        problemRepository.save(problem);
    }
}
