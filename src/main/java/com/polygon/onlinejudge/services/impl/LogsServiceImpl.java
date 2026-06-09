package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.logs.AuthorSolutionLogsResponse;
import com.polygon.onlinejudge.dto.logs.CompletionStatusItem;
import com.polygon.onlinejudge.dto.logs.CompletionStatusResponse;
import com.polygon.onlinejudge.dto.logs.LogsResponse;
import com.polygon.onlinejudge.entities.Logs;
import com.polygon.onlinejudge.entities.ProblemStatement;
import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.mappers.LogsMapper;
import com.polygon.onlinejudge.policy.ProblemPolicy;
import com.polygon.onlinejudge.repositories.LogsRepository;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.repositories.TestCaseRepository;
import com.polygon.onlinejudge.repositories.TestGroupRepository;
import com.polygon.onlinejudge.services.LogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogsServiceImpl implements LogsService {

    private final LogsRepository logsRepository;
    private final LogsMapper logsMapper;
    private final ProblemVersionRepository problemVersionRepository;
    private final TestGroupRepository testGroupRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProblemPolicy problemPolicy;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveLog(Logs log) {
        logsRepository.save(log);
    }

    @Override
    public List<LogsResponse> getAllLogs() {
        return logsRepository.findAll().stream().map(logsMapper::toDto).toList();
    }

    @Override
    public List<AuthorSolutionLogsResponse> getAuthorSolutionLogs(UUID problemVersionId, String email) {
        ProblemVersion version = problemVersionRepository.findById(problemVersionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + problemVersionId));
        problemPolicy.checkIsOwner(version, email);
        return logsRepository.findAuthorSolutionLogsByProblemVersionId(problemVersionId)
                .stream().map(logsMapper::toAuthorSolutionLogsDto).toList();
    }

    @Override
    public CompletionStatusResponse getCompletionStatus(UUID versionId, String email) {
        ProblemVersion version = problemVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
        problemPolicy.checkIsOwner(version, email);

        return new CompletionStatusResponse(
                statementStatus(version),
                testsStatus(versionId),
                solutionStatus(version),
                validationStatus(versionId)
        );
    }

    private CompletionStatusItem statementStatus(ProblemVersion version) {
        ProblemStatement stmt = version.getProblemStatement();
        boolean complete = stmt != null
                && !isBlank(stmt.getDescription())
                && !isBlank(stmt.getInputDescription())
                && !isBlank(stmt.getOutputDescription());
        return new CompletionStatusItem(complete, complete ? "Complete" : "Missing required fields");
    }

    private CompletionStatusItem testsStatus(UUID versionId) {
        int groupCount = testGroupRepository.findAllByVersion_Id(versionId).size();
        int testCount = testCaseRepository.countByGroup_Version_Id(versionId);
        boolean complete = groupCount > 0 && testCount > 0;
        return new CompletionStatusItem(complete,
                complete ? testCount + " tests, " + groupCount + " groups" : "No tests yet");
    }

    private CompletionStatusItem solutionStatus(ProblemVersion version) {
        boolean complete = version.getAuthorSolution() != null;
        return new CompletionStatusItem(complete, complete ? "Ready" : "No file yet");
    }

    private CompletionStatusItem validationStatus(UUID versionId) {
        List<Logs> logs = logsRepository.findAuthorSolutionLogsByProblemVersionId(versionId);
        if (logs.isEmpty()) {
            return new CompletionStatusItem(false, "Not validated yet");
        }
        long passed = logs.stream().filter(l -> "Accepted".equals(l.getStatus())).count();
        long total = logs.size();
        boolean complete = passed == total;
        return new CompletionStatusItem(complete, passed + " / " + total + " tests passed");
    }

    @Override
    @Transactional
    public void clearValidationLogs(UUID versionId) {
        logsRepository.deleteAllByVersion_Id(versionId);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
