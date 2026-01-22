package com.polygon.onlinejudge.services.impl;

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
}
