package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.problem.AuthorSolutionRequest;
import com.polygon.onlinejudge.dto.problem.AuthorSolutionResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.entities.AuthorSolution;
import com.polygon.onlinejudge.entities.Problem;
import com.polygon.onlinejudge.entities.ProblemStatement;
import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.mappers.AuthorSolutionMapper;
import com.polygon.onlinejudge.mappers.ProblemStatementMapper;
import com.polygon.onlinejudge.mappers.ProblemVersionMapper;
import com.polygon.onlinejudge.policy.ProblemVersionPolicy;
import com.polygon.onlinejudge.repositories.AuthorSolutionRepository;
import com.polygon.onlinejudge.repositories.ProblemRepository;
import com.polygon.onlinejudge.repositories.ProblemStatementRepository;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.services.ProblemVersionService;
import com.polygon.onlinejudge.services.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProblemVersionServiceImpl implements ProblemVersionService {

    private final ProblemVersionPolicy problemVersionPolicy;
    private final ProblemRepository problemRepository;
    private final ProblemVersionRepository problemVersionRepository;
    private final ProblemVersionMapper problemVersionMapper;
    private final ProblemStatementRepository problemStatementRepository;
    private final ProblemStatementMapper problemStatementMapper;
    private final AuthorSolutionRepository authorSolutionRepository;
    private final AuthorSolutionMapper authorSolutionMapper;
    private final S3Service s3Service;

    @Override
    public ProblemVersionResponse createVersion(UUID problemId, ProblemVersionRequest request) {
        problemVersionPolicy.requireLatestVersionVerified(problemId);
        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new IllegalArgumentException("Problem with id: " + problemId + " not found"));
        int version = problemVersionRepository.countByProblem(problem);

        ProblemVersion problemVersion = ProblemVersion.builder()
                .problem(problem)
                .version(version+1)
                .status(Status.DRAFT)
                .timeLimitMs(
                        request.getTimeLimitMs() != null
                            ? request.getTimeLimitMs()
                            : 1000L
                )
                .memoryLimitMb(
                        request.getMemoryLimitMb() != null
                                ? request.getMemoryLimitMb()
                                : 256L
                )
                .scoringType(request.getScoringType())
                .build();
        ProblemVersion newProblemVersion = problemVersionRepository.save(problemVersion);

        ProblemStatement problemStatement = ProblemStatement.builder()
                .version(newProblemVersion)
                .build();
        problemStatement = problemStatementRepository.save(problemStatement);

        newProblemVersion.setProblemStatement(problemStatement);
        problemVersionRepository.save(newProblemVersion);

        return problemVersionMapper.toDto(newProblemVersion);
    }

    @Override
    public ProblemVersionResponse getVersion(UUID versionId) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        return problemVersionMapper.toDto(problemVersion);
    }

    @Override
    public ProblemVersionResponse updateVersion(UUID versionId, ProblemVersionRequest request) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));

        if(problemVersion.getStatus() != Status.DRAFT) {
            throw new IllegalStateException("Problem version with id: " + versionId + " is already verified, you cannot change");
        }

        problemVersionMapper.updateProblem(request, problemVersion);
        ProblemVersion newVersion = problemVersionRepository.save(problemVersion);

        return problemVersionMapper.toDto(newVersion);
    }

    @Override
    public void finalizeVersion(UUID versionId) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        problemVersionPolicy.checkVersion(problemVersion);

        problemVersion.setStatus(Status.VERIFIED);
        problemVersionRepository.save(problemVersion);
    }

    @Override
    public AuthorSolutionResponse addAuthorSolution(UUID versionId, AuthorSolutionRequest request) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));

        if (problemVersion.getStatus() != Status.DRAFT) {
            throw new IllegalStateException("Cannot add tests to non-DRAFT version");
        }

        if (request.getSourceCode() == null || request.getSourceCode().isBlank()) {
            throw new IllegalArgumentException("Input is empty");
        }

        UUID uuid = UUID.randomUUID();
        String ext = switch (request.getLanguage()) {
            case JAVA -> "java";
            case CPP -> "cpp";
            case PY -> "py";
        };

        String key = String.format(
                "polygon/versions/%s/solutions/author/%s",
                versionId,
                uuid
        );

        String inputKey = key + ("."+ext);

        String url = s3Service.putText(inputKey, request.getSourceCode());

        AuthorSolution solution = AuthorSolution.builder()
                .version(problemVersion)
                .language(request.getLanguage())
                .sourceCode(url)
                .build();

        authorSolutionRepository.save(solution);
        return authorSolutionMapper.toDto(solution);
    }

    @Override
    public ProblemStatementResponse updateStatement(UUID versionId, ProblemStatementRequest request) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        problemVersionPolicy.checkVersion(problemVersion);

        ProblemStatement oldProblemStatement = problemStatementRepository.findProblemStatementByVersion_Id(versionId).orElseThrow(() ->  new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        oldProblemStatement.setDescription(request.getDescription());
        oldProblemStatement.setInputDescription(request.getInputDescription());
        oldProblemStatement.setOutputDescription(request.getOutputDescription());
        oldProblemStatement.setNotes(request.getNotes());
        ProblemStatement updatedProblemStatement = problemStatementRepository.save(oldProblemStatement);

        problemVersion.setProblemStatement(updatedProblemStatement);
        problemVersionRepository.save(problemVersion);

        return problemStatementMapper.toDto(updatedProblemStatement);
    }

    @Override
    public ProblemStatementResponse getStatement(UUID versionId) {
        ProblemStatement oldProblemStatement = problemStatementRepository.findProblemStatementByVersion_Id(versionId).orElseThrow(() ->  new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        return problemStatementMapper.toDto(oldProblemStatement);
    }

}
