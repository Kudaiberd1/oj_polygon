package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.context.UserContext;
import com.polygon.onlinejudge.dto.pagination.PaginationParams;
import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.dto.problem.ProblemSummaryResponse;
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
    public Page<ProblemSummaryResponse> getAllProblems(String email, PaginationParams paginationParams) {
        User user = userContext.getUser(email);
        return problemRepository.findAllByOwnerId(user.getId(), paginationParams.toPageable())
                .map(problem -> {
                    int total = problemVersionRepository.countByProblem_Id(problem.getId());
                    return problemVersionRepository.findFirstByProblem_IdOrderByVersionDesc(problem.getId())
                            .map(latest -> ProblemSummaryResponse.builder()
                                    .id(problem.getId())
                                    .title(problem.getTitle())
                                    .email(email)
                                    .createdAt(problem.getCreatedAt())
                                    .totalVersions(total)
                                    .latestVersionNumber(latest.getVersion())
                                    .latestVersionStatus(latest.getStatus() != null ? latest.getStatus().name() : null)
                                    .build())
                            .orElseGet(() -> ProblemSummaryResponse.builder()
                                    .id(problem.getId())
                                    .title(problem.getTitle())
                                    .email(email)
                                    .createdAt(problem.getCreatedAt())
                                    .totalVersions(0)
                                    .latestVersionNumber(null)
                                    .latestVersionStatus(null)
                                    .build());
                });
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
