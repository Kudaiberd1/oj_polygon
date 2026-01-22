package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.problem.ProblemRequestDto;
import com.polygon.onlinejudge.dto.problem.ProblemResponseDto;
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
    public List<ProblemResponseDto> getAllproblems() {
        List<Problem> problems = problemRepository.findAll();
        return problems.stream().map(problemMapper::toDto).toList();
    }

    @Override
    public ProblemResponseDto getById(String id) {
        Problem problem = problemRepository.findById(id).orElseThrow();

        return problemMapper.toDto(problem);
    }

    @Override
    public ProblemResponseDto createProblem(ProblemRequestDto problem) {
        Problem newProblem = Problem.builder()
                .title(problem.getTitle())
                .description(problem.getDescription())
                .inputDescription(problem.getInputDescription())
                .outputDescription(problem.getOutputDescription())
                .timeLimitMs(problem.getTimeLimitMs())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .allowedLanguages(problem.getAllowedLanguages())
                .scoringType(problem.getScoringType())
                .ownerId(problem.getOwnerId())
                .status(problem.getStatus())
                .build();

        problemRepository.save(newProblem);
        return problemMapper.toDto(newProblem);
    }

    @Override
    public void updateProblem(ProblemRequestDto request, String id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        problemMapper.updateProblem(request, problem);

        problemRepository.save(problem);
    }
}
