package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.judge.Judge0SubmissionRequest;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;
import com.polygon.onlinejudge.entities.*;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.repositories.LogsRepository;
import com.polygon.onlinejudge.repositories.TestCaseRepository;
import com.polygon.onlinejudge.repositories.TestGroupRepository;
import com.polygon.onlinejudge.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void verifyVersion(ProblemVersion problemVersion) {
        List<String> errors = new ArrayList<>();

        if (problemVersion == null) {
            throw new IllegalArgumentException("Problem version is null");
        }

        if (problemVersion.getStatus() != Status.DRAFT) {
            errors.add("Only DRAFT versions can be verified");
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

        Map<UUID, List<String>> testGroupResults = new HashMap<>();

        for(TestGroup group : testGroups){
            List<String> results = new ArrayList<>();
            for(var test : group.getTests()){
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

                Logs logg = Logs.builder()
                        .version(problemVersion)
                        .status(response.getStatus() != null ? response.getStatus().getDescription() : "Unknown")
                        .log(response.getStderr() != null ? response.getStderr() : response.getCompile_output() != null ? response.getCompile_output() : "")
                        .message(response.getMessage() != null ? response.getMessage() : "Unknown")
                        .time(response.getTime() != null ? response.getTime() : "")
                        .memory(response.getMemory() != null ? response.getMemory() : 0L)
                        .build();

                logsService.saveLog(logg);

                if(response.getStatus() == null){
                    throw new IllegalStateException("Test " + test.getId() + ": Author solution failed with unknown error");
                }

                if(response.getStatus().getId() != 3){

                    throw new IllegalStateException("Test " + test.getId() + ": Author solution failed with status " + response.getStatus().getDescription());
                } else {
                    results.add(response.getStdout());
                }
            }
            testGroupResults.put(group.getId(), results);
        }

        setTestCaseOutput(testGroupResults);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }


    @Override
    public void setTestCaseOutput(Map<UUID, List<String>> testGroupOutputs) {

        for (Map.Entry<UUID, List<String>> entry : testGroupOutputs.entrySet()) {
            UUID testGroupId = entry.getKey();
            List<String> outputs = entry.getValue();

            TestGroup group = testGroupRepository.findById(testGroupId)
                    .orElseThrow(() -> new IllegalArgumentException("TestGroup not found"));

            List<TestCase> testCases = testCaseRepository.findTestCasesByGroup_Id(testGroupId);

            List<TestCase> updatedTest = new ArrayList<>();
            for (int i = 0; i < testCases.size(); i++) {
                TestCase tc = testCases.get(i);
                String outputKey = String.format(
                        "polygon/%s/%03d.out",
                        group.getId(),
                        tc.getOrderId()
                );

                String url = s3Service.putText(outputKey, outputs.get(i));

                tc.setOutputPath(url);
                updatedTest.add(tc);
            }
            testCaseRepository.saveAll(updatedTest);
        }
    }
}
