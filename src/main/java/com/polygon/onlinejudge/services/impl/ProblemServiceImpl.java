package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.context.UserContext;
import com.polygon.onlinejudge.dto.pagination.PaginationParams;
import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.entities.Problem;
import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.User;
import com.polygon.onlinejudge.mappers.ProblemMapper;
import com.polygon.onlinejudge.mappers.ProblemVersionMapper;
import com.polygon.onlinejudge.policy.ProblemPolicy;
import com.polygon.onlinejudge.repositories.ProblemRepository;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.services.ProblemService;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;
    private final ProblemPolicy problemPolicy;
    private final ProblemVersionRepository problemVersionRepository;
    private final ProblemVersionMapper problemVersionMapper;
    private final UserContext userContext;

    @Override
    public Page<ProblemResponse> getAllProblems(String email, PaginationParams paginationParams) {
        User user = userContext.getUser(email);
        return problemRepository.findAllByOwnerId(user.getId(), paginationParams.toPageable())
                .map(problemMapper::toDto);
    }

    @Override
    public ProblemResponse getById(UUID id, String email) {
        Problem problem = problemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Problem with id: " + id + " not found"));
        problemPolicy.checkIsOwner(problem, email);

        return problemMapper.toDto(problem);
    }

    @Override
    public ProblemResponse createProblem(String email, ProblemRequest problemRequest) {
        User user = userContext.getUser(email);

        Problem problem = Problem.builder()
                .title(problemRequest.getTitle())
                .ownerId(user.getId())
                .build();

        return problemMapper.toDto(problemRepository.save(problem));
    }

    @Override
    public List<ProblemVersionResponse> getProblemVersions(UUID problemId) {
        List<ProblemVersion> versions = problemVersionRepository.findAllByProblem_Id(problemId);

        return versions.stream().map(problemVersionMapper::toDto).toList();
    }

}
