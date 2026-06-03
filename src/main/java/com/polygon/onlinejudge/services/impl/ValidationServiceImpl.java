package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.judge.Judge0SubmissionRequest;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;
import com.polygon.onlinejudge.entities.*;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.repositories.TestCaseRepository;
import com.polygon.onlinejudge.repositories.TestGroupRepository;
import com.polygon.onlinejudge.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationServiceImpl implements ValidationService {

    private final TestGroupRepository testGroupRepository;
    private final S3Service s3Service;
    private final Judge0ClientService judge0ClientService;
    private final LogsService logsService;
    private final TestCaseRepository testCaseRepository;
    private final ProblemVersionRepository problemVersionRepository;

    @Lazy
    @Autowired
    private ValidationService self;

    @Override
    public void verifyVersion(ProblemVersion problemVersion) {
        List<String> errors = new ArrayList<>();

        if (problemVersion == null) {
            throw new IllegalArgumentException("Problem version is null");
        }

        if (problemVersion.getTimeLimitMs() == null || problemVersion.getTimeLimitMs() <= 0) {
            errors.add("Time limit must be greater than 0");
        }

        if (problemVersion.getMemoryLimitMb() == null || problemVersion.getMemoryLimitMb() <= 0) {
            errors.add("Memory limit must be greater than 0");
        }

        if (problemVersion.getScoringType() == null) {
            errors.add("Scoring type is required");
        }

        ProblemStatement statement = problemVersion.getProblemStatement();
        if (statement == null) {
            errors.add("Problem statement is missing");
        } else {
            if (isBlank(statement.getDescription())) {
                errors.add("Problem description is required");
            }
            if (isBlank(statement.getInputDescription())) {
                errors.add("Input description is required");
            }
            if (isBlank(statement.getOutputDescription())) {
                errors.add("Output description is required");
            }
        }

        AuthorSolution solution = problemVersion.getAuthorSolution();
        if (solution == null) {
            errors.add("Author solution is missing");
        } else {
            if (solution.getLanguage() == null) {
                errors.add("Author solution language is required");
            }
            if (isBlank(solution.getSourceCode())) {
                errors.add("Author solution source code is empty");
            }
        }

        List<TestGroup> groups = testGroupRepository.findAllByVersion_Id(problemVersion.getId());
        if (groups.isEmpty()) {
            errors.add("At least one test group is required");
        } else {
            for (int i = 0; i < groups.size(); i++) {
                TestGroup group = groups.get(i);

                if (group.getPoints() <= 0) {
                    errors.add("Test group " + (i + 1) + " must have points greater than 0");
                }

                if (group.getTests() == null || group.getTests().isEmpty()) {
                    errors.add("Test group " + (i + 1) + " must contain at least one test");
                }
            }
        }

        problemVersion.setStatus(Status.REJECTED);
        problemVersionRepository.save(problemVersion);

        if (!errors.isEmpty()) {
            throw new IllegalStateException(String.join("; ", errors));
        }
    }

    @Override
    public void verifyAuthorSolution(ProblemVersion problemVersion) {
        AuthorSolution solution = problemVersion.getAuthorSolution();

        String sourceCode = s3Service.getInput(solution.getSourceCode());
        long memoryLimit = problemVersion.getMemoryLimitMb() * 1024;
        Double cpuLimit = problemVersion.getTimeLimitMs() / 1000.0;
        Double wallTimeLimit = cpuLimit * 2;
        int languageId = switch (solution.getLanguage()) {
            case JAVA -> 62;
            case CPP -> 54;
            case PY -> 71;
        };

        List<TestGroup> testGroups = testGroupRepository.findAllByVersion_Id(problemVersion.getId());

        Map<Long, String> testOutputs = new HashMap<>();
        List<String> failures = new ArrayList<>();

        for (TestGroup group : testGroups) {
            for (var test : group.getTests()) {
                String input = s3Service.getInput(test.getInputPath());
                Judge0SubmissionRequest request = Judge0SubmissionRequest.builder()
                        .source_code(sourceCode)
                        .stdin(input)
                        .cpu_time_limit(cpuLimit)
                        .wall_time_limit(wallTimeLimit)
                        .memory_limit(memoryLimit)
                        .language_id(languageId)
                        .build();

                Judge0SubmissionResponse response = judge0ClientService.runSubmission(request);

                Logs log = Logs.builder()
                        .version(problemVersion)
                        .orderId(test.getOrderId())
                        .testGroupId(group.getId())
                        .status(response.getStatus() != null ? response.getStatus().getDescription() : "Unknown")
                        .log(response.getStderr() != null ? response.getStderr() : response.getCompile_output() != null ? response.getCompile_output() : "")
                        .message(response.getMessage() != null ? response.getMessage() : "")
                        .time(response.getTime() != null ? response.getTime() : "")
                        .memory(response.getMemory() != null ? response.getMemory() : 0L)
                        .build();

                logsService.saveLog(log);

                if (response.getStatus() != null && response.getStatus().getId() == 3) {
                    testOutputs.put(test.getId(), response.getStdout());
                } else {
                    String desc = response.getStatus() != null ? response.getStatus().getDescription() : "Unknown error";
                    failures.add("Test " + test.getOrderId() + ": " + desc);
                }
            }
        }



        self.clearTestCaseOutputs(problemVersion);
        self.setTestCaseOutput(testOutputs);
        log.info(testOutputs.toString());

        if (!failures.isEmpty()) {
            problemVersion.setStatus(Status.REJECTED);
            problemVersionRepository.save(problemVersion);
            throw new IllegalStateException("Author solution failed on " + failures.size() + " test(s): " + String.join("; ", failures));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearTestCaseOutputs(ProblemVersion problemVersion) {
        List<TestCase> toClean = testCaseRepository.findAllByGroup_Version_Id(problemVersion.getId())
                .stream()
                .filter(tc -> tc.getOutputPath() != null)
                .toList();
        toClean.forEach(tc -> {
            s3Service.delete(tc.getOutputPath());
            tc.setOutputPath(null);
        });
        testCaseRepository.saveAll(toClean);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setTestCaseOutput(Map<Long, String> testOutputs) {
        List<TestCase> testCases = new ArrayList<>();
        for (Map.Entry<Long, String> entry : testOutputs.entrySet()) {
            TestCase tc = testCaseRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("TestCase not found: " + entry.getKey()));

            TestGroup group = tc.getGroup();
            UUID problemId = group.getVersion().getProblem().getId();
                UUID versionId = group.getVersion().getId();

            String outputKey = String.format(
                    "problems/%s/versions/%s/tests/%s/%03d.out",
                    problemId,
                    versionId,
                    group.getId(),
                    tc.getOrderId()
            );

            tc.setOutputPath(s3Service.putText(outputKey, entry.getValue()));
            testCases.add(tc);
        }
        testCaseRepository.saveAll(testCases);
    }
}
