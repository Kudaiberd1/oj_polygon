package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.problem.ProblemResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProblemService {

    List<ProblemResponseDto> getAllproblems();

    ProblemResponseDto getById(String id);
}
