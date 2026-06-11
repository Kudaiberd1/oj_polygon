package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.snapshot.SnapshotResponse;
import com.polygon.onlinejudge.dto.snapshot.SnapshotTestCaseResponse;
import com.polygon.onlinejudge.entities.*;
import com.polygon.onlinejudge.repositories.*;
import com.polygon.onlinejudge.services.S3Service;
import com.polygon.onlinejudge.services.SnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SnapshotServiceImpl implements SnapshotService {

    private final SnapshotRepository snapshotRepository;
    private final SnapshotTestCaseRepository snapshotTestCaseRepository;
    private final ProblemVersionRepository problemVersionRepository;
    private final TestCaseRepository testCaseRepository;
    private final S3Service s3Service;

    @Override
    public UUID getSnapshot(UUID problemId, UUID versionId) {
        Snapshot snapshot = snapshotRepository.findByProblemIdAndSourceVersionId(problemId, versionId);
        if (snapshot == null) throw new IllegalArgumentException("Snapshot not found");
        return snapshot.getId();
    }

    @Override
    public void createSnapshot(UUID problemId, String createdBy) {
        ProblemVersion version = problemVersionRepository.findLastVersion(problemId)
                .orElseThrow(() -> new IllegalArgumentException("No verified versions found for problem: " + problemId));

        if (snapshotRepository.findByProblemIdAndSourceVersionId(problemId, version.getId()) != null) {
            throw new IllegalStateException("Snapshot already exists for last version");
        }

        ProblemStatement stmt = version.getProblemStatement();
        AuthorSolution solution = version.getAuthorSolution();

        Snapshot snapshot = Snapshot.builder()
                .problemId(problemId)
                .sourceVersionId(version.getId())
                .sourceVersionNumber(version.getVersion())
                .timeLimitMs(version.getTimeLimitMs())
                .memoryLimitMb(version.getMemoryLimitMb())
                .statementDescription(stmt != null ? stmt.getDescription() : null)
                .statementInput(stmt != null ? stmt.getInputDescription() : null)
                .statementOutput(stmt != null ? stmt.getOutputDescription() : null)
                .statementNotes(stmt != null ? stmt.getNotes() : null)
                .createdBy(createdBy)
                .isCommitted(false)
                .build();
        snapshot = snapshotRepository.save(snapshot);

        UUID snapshotId = snapshot.getId();

        // Deep copy test cases to S3 + save SnapshotTestCase rows
        List<TestCase> testCases = testCaseRepository.findAllByGroup_Version_Id(version.getId());
        for (TestCase tc : testCases) {
            String inputContent = s3Service.getInput(tc.getInputPath());
            String outputContent = s3Service.getInput(tc.getOutputPath());

            String inputKey = String.format("snapshots/%s/tests/%d/input.txt", snapshotId, tc.getOrderId());
            String outputKey = String.format("snapshots/%s/tests/%d/output.txt", snapshotId, tc.getOrderId());

            s3Service.putText(inputKey, inputContent);
            s3Service.putText(outputKey, outputContent);

            snapshotTestCaseRepository.save(SnapshotTestCase.builder()
                    .snapshotId(snapshotId)
                    .orderId(tc.getOrderId())
                    .groupId(tc.getGroup().getId())
                    .inputS3Key(inputKey)
                    .outputS3Key(outputKey)
                    .build());
        }

        // Deep copy author solution to S3
        if (solution != null) {
            String ext = switch (solution.getLanguage()) {
                case JAVA -> "java";
                case CPP -> "cpp";
                case PY -> "py";
            };
            String solutionContent = s3Service.getInput(solution.getSourceCode());
            String solutionKey = String.format("snapshots/%s/solution.%s", snapshotId, ext);
            s3Service.putText(solutionKey, solutionContent);
        }
    }

    @Override
    public List<SnapshotResponse> getAllSnapshots(UUID problemId) {
        return snapshotRepository.findAllByProblemId(problemId).stream()
                .map(s -> {
                    List<SnapshotTestCaseResponse> testCases = snapshotTestCaseRepository
                            .findAllBySnapshotId(s.getId()).stream()
                            .map(tc -> SnapshotTestCaseResponse.builder()
                                    .id(tc.getId())
                                    .build())
                            .toList();
                    return SnapshotResponse.builder()
                            .snapshotId(s.getId())
                            .sourceVersionNumber(s.getSourceVersionNumber())
                            .timeLimitMs(s.getTimeLimitMs())
                            .memoryLimitMb(s.getMemoryLimitMb())
                            .createdBy(s.getCreatedBy())
                            .createdAt(s.getCreatedAt())
                            .isCommitted(s.getIsCommitted())
                            .testCases(testCases)
                            .build();
                })
                .toList();
    }

    @Override
    public void commitChanges(UUID problemId) {
        Snapshot snapshot = snapshotRepository.findFirstByProblemIdOrderBySourceVersionNumberDesc(problemId)
                .orElseThrow(() -> new IllegalArgumentException("No snapshot found for problem: " + problemId));
        if (Boolean.TRUE.equals(snapshot.getIsCommitted())) {
            throw new IllegalStateException("Latest snapshot is already committed");
        }
        snapshot.setIsCommitted(true);
        snapshotRepository.save(snapshot);
    }
}
