package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.judge.Judge0SubmissionRequest;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;
import com.polygon.onlinejudge.dto.problem.AuthorSolutionRequest;
import com.polygon.onlinejudge.dto.problem.AuthorSolutionResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.dto.test.TestCaseResponse;
import com.polygon.onlinejudge.entities.*;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.mappers.AuthorSolutionMapper;
import com.polygon.onlinejudge.mappers.ProblemStatementMapper;
import com.polygon.onlinejudge.mappers.ProblemVersionMapper;
import com.polygon.onlinejudge.mappers.TestCaseMapper;
import com.polygon.onlinejudge.policy.ProblemVersionPolicy;
import com.polygon.onlinejudge.repositories.*;
import com.polygon.onlinejudge.services.Judge0ClientService;
import com.polygon.onlinejudge.services.ProblemVersionService;
import com.polygon.onlinejudge.services.S3Service;
import com.polygon.onlinejudge.services.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private final ValidationService validationService;
    private final TestGroupRepository testGroupRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestCaseMapper testCaseMapper;
    private final Judge0ClientService judge0ClientService;

    @Lazy
    @Autowired
    private ProblemVersionService self;

    @Override
    public ProblemVersionResponse createVersion(UUID problemId, ProblemVersionRequest request) {
        problemVersionPolicy.requireLatestVersionVerified(problemId);

        ProblemVersion latest = problemVersionRepository.findFirstByProblem_IdOrderByVersionDesc(problemId).orElse(null);
        if (latest != null) {
            return problemVersionMapper.toDto(copyVersion(latest));
        }

        return problemVersionMapper.toDto(createBlankVersion(problemId, request));
    }

    private ProblemVersion createBlankVersion(UUID problemId, ProblemVersionRequest request) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new IllegalArgumentException("Problem with id: " + problemId + " not found"));
        int v = problemVersionRepository.findLastVersion(problemId)
                .map(ProblemVersionRepository.ProblemVersionView::getVersion)
                .orElse(0);

        ProblemVersion problemVersion = ProblemVersion.builder()
                .problem(problem)
                .version(v + 1)
                .status(Status.DRAFT)
                .timeLimitMs(request.getTimeLimitMs() != null ? request.getTimeLimitMs() : 1000L)
                .memoryLimitMb(request.getMemoryLimitMb() != null ? request.getMemoryLimitMb() : 256L)
                .scoringType(request.getScoringType())
                .build();
        ProblemVersion newProblemVersion = problemVersionRepository.save(problemVersion);

        ProblemStatement problemStatement = ProblemStatement.builder()
                .version(newProblemVersion)
                .build();
        problemStatement = problemStatementRepository.save(problemStatement);

        newProblemVersion.setProblemStatement(problemStatement);
        return problemVersionRepository.save(newProblemVersion);
    }

    @Override
    public ProblemVersionResponse getVersion(UUID versionId) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        return problemVersionMapper.toDto(problemVersion);
    }

    @Override
    public ProblemVersionResponse updateVersion(UUID versionId, ProblemVersionRequest request) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));

        problemVersionMapper.updateProblem(request, problemVersion);
        ProblemVersion newVersion = problemVersionRepository.save(problemVersion);

        return problemVersionMapper.toDto(newVersion);
    }

    @Override
    public void finalizeVersion(UUID versionId) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        if(problemVersion.getStatus() == Status.VERIFIED){
            throw new IllegalStateException("Only DRAFT or REJECTED version can be finalized");
        }

        try {
            validationService.verifyVersion(problemVersion);
            validationService.verifyAuthorSolution(problemVersion);
        } catch (Exception e) {
            self.markRejected(versionId);
            throw e;
        }

        problemVersion.setStatus(Status.VERIFIED);
        problemVersionRepository.save(problemVersion);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markRejected(UUID versionId) {
        problemVersionRepository.findById(versionId).ifPresent(v -> {
            v.setStatus(Status.REJECTED);
            problemVersionRepository.save(v);
        });
    }

    @Override
    public AuthorSolutionResponse addAuthorSolution(UUID versionId, AuthorSolutionRequest request) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));

        if (request.getSourceCode() == null || request.getSourceCode().isBlank()) {
            throw new IllegalArgumentException("Input is empty");
        }

        if(problemVersion.getAuthorSolution() != null){
            throw new IllegalStateException("Already has an author solution, you cannot add another one");
        }

        String ext = switch (request.getLanguage()) {
            case JAVA -> "java";
            case CPP -> "cpp";
            case PY -> "py";
        };

        String inputKey = String.format(
                "problems/%s/versions/%s/solution.%s",
                problemVersion.getProblem().getId(),
                versionId,
                ext
        );

        String url = s3Service.putText(inputKey, request.getSourceCode());

        AuthorSolution solution = AuthorSolution.builder()
                .version(problemVersion)
                .language(request.getLanguage())
                .sourceCode(url)
                .build();

        problemVersion.setAuthorSolution(solution);

        authorSolutionRepository.save(solution);
        problemVersionRepository.save(problemVersion);
        return authorSolutionMapper.toDto(solution);
    }

    @Override
    public AuthorSolutionResponse getAuthorSolution(UUID versionId) {
        AuthorSolution solution = authorSolutionRepository.findByVersion_Id(versionId).orElseThrow(() -> new IllegalArgumentException("Author solution not found"));

        return authorSolutionMapper.toDto(solution);
    }

    @Override
    public void updateAuthorSolution(UUID versionId, AuthorSolutionRequest request) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));
        AuthorSolution solution = authorSolutionRepository.findByVersion_Id(versionId).orElseThrow(() -> new IllegalArgumentException("Author solution not found"));

         if (solution.getVersion().getStatus() == Status.VERIFIED) {
            throw new IllegalStateException("Cannot update author solution of non-DRAFT version");
         }

        if (request.getSourceCode() == null || request.getSourceCode().isBlank()) {
            throw new IllegalArgumentException("Input is empty");
        }

        String ext = switch (request.getLanguage()) {
            case JAVA -> "java";
            case CPP -> "cpp";
            case PY -> "py";
        };

        String inputKey = String.format(
                "problems/%s/versions/%s/solution.%s",
                problemVersion.getProblem().getId(),
                versionId,
                ext
        );

        String url = s3Service.putText(inputKey, request.getSourceCode());

        solution.setLanguage(request.getLanguage());
        solution.setSourceCode(url);

        problemVersion.setAuthorSolution(solution);
        problemVersionRepository.save(problemVersion);
        authorSolutionRepository.save(solution);
    }

    @Override
    public ProblemStatementResponse updateStatement(UUID versionId, ProblemStatementRequest request) {
        ProblemVersion problemVersion = problemVersionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Problem version with id: " + versionId + " not found"));

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

    public ProblemVersion copyVersion(ProblemVersion oldVersion){
        ProblemVersion newVersion = createBlankVersion(oldVersion.getProblem().getId(), ProblemVersionRequest.builder()
                .timeLimitMs(oldVersion.getTimeLimitMs())
                .memoryLimitMb(oldVersion.getMemoryLimitMb())
                .scoringType(oldVersion.getScoringType())
                .build()
        );

        // Copy problem statement
        ProblemStatement oldProblemStatement = problemStatementRepository.findProblemStatementByVersion_Id(oldVersion.getId()).orElseThrow(() ->  new IllegalArgumentException("Problem version with id: " + oldVersion.getId() + " not found"));
        updateStatement(newVersion.getId(), ProblemStatementRequest.builder()
                .description(oldProblemStatement.getDescription())
                .inputDescription(oldProblemStatement.getInputDescription())
                .outputDescription(oldProblemStatement.getOutputDescription())
                .notes(oldProblemStatement.getNotes())
                .build()
        );

        // Copy author solution
        AuthorSolution oldAuthorSolution = authorSolutionRepository.findByVersion_Id(oldVersion.getId()).orElseThrow(() -> new IllegalArgumentException("Author solution not found"));
        addAuthorSolution(newVersion.getId(), AuthorSolutionRequest.builder()
                .language(oldAuthorSolution.getLanguage())
                .sourceCode(s3Service.getInput(oldAuthorSolution.getSourceCode()))
                .build()
        );

        // Copy test groups
        List<TestGroup> testGroups = testGroupRepository.findAllByVersion_Id(oldVersion.getId());
        List<TestGroup> newTestGroups = new ArrayList<>();
        for (TestGroup testGroup : testGroups) {
            TestGroup newTestGroup = TestGroup.builder()
                    .version(newVersion)
                    .points(testGroup.getPoints())
                    .build();
            newTestGroups.add(newTestGroup);
        }
        testGroupRepository.saveAll(newTestGroups);

        // Copy test cases
        UUID problemId = newVersion.getProblem().getId();
        UUID newVersionId = newVersion.getId();
        for (int i = 0; i < testGroups.size(); i++) {
            TestGroup oldGroup = testGroups.get(i);
            TestGroup newGroup = newTestGroups.get(i);
            List<TestCase> testCases = testCaseRepository.findTestCasesByGroup_Id(oldGroup.getId());

            List<TestCase> newTestCases = new ArrayList<>();
            for (TestCase testCase : testCases) {
                String inputKey = String.format(
                        "problems/%s/versions/%s/tests/%s/%03d.in",
                        problemId, newVersionId, newGroup.getId(), testCase.getOrderId()
                );
                String inputContent = s3Service.getInput(testCase.getInputPath());
                String newInputPath = s3Service.putText(inputKey, inputContent);

                newTestCases.add(TestCase.builder()
                        .group(newGroup)
                        .problemVersion(newVersion)
                        .orderId(testCase.getOrderId())
                        .inputPath(newInputPath)
                        .build());
            }
            testCaseRepository.saveAll(newTestCases);
        }

        return newVersion;
    }


    @Override
    public Judge0SubmissionResponse testCode(UUID solutionId, String test) {
        AuthorSolution solutionCode = authorSolutionRepository.findById(solutionId).orElseThrow(() -> new IllegalArgumentException("Solution code not found"));

        int languageId = switch (solutionCode.getLanguage()) {
            case JAVA -> 62;
            case CPP -> 54;
            case PY -> 71;
        };
        String sourceCode = s3Service.getInput(solutionCode.getSourceCode());

        Judge0SubmissionRequest request = Judge0SubmissionRequest.builder()
                .source_code(sourceCode)
                .stdin(test)
                .language_id(languageId)
                .build();

        Judge0SubmissionResponse response = judge0ClientService.runSubmission(request);

        return response;
    }

    @Override
    public List<TestCaseResponse> getExmapleTestCases(UUID versionId) {
        List<TestCase> testCases = testCaseRepository.findTestCasesByIsExampleAndProblemVersion_Id(true, versionId);
        return testCases.stream().map(testCaseMapper::toDto).toList();
    }
}
