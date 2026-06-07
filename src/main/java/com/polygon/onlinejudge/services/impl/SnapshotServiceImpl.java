package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.snapshot.SnapshotResponse;
import com.polygon.onlinejudge.entities.Snapshot;
import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.repositories.SnapshotRepository;
import com.polygon.onlinejudge.services.SnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private final SnapshotRepository snapshotRepository;
    private final ProblemVersionRepository problemVersionRepository;

    @Override
    public UUID getSnapshot(UUID problemId, UUID versionId) {
        Snapshot snapshot = snapshotRepository.findByProblem_IdAndProblemVersion_Id(problemId, versionId);
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot not found");
        }
        return snapshot.getId();
    }

    @Override
    public void createSnapshot(UUID problemId) {
        ProblemVersion version = problemVersionRepository.findLastVersion(problemId).orElseThrow(() -> new IllegalArgumentException("No verified versions found for problem: " + problemId));

        if (snapshotRepository.findByProblem_IdAndProblemVersion_Id(problemId, version.getId()) != null) {
            throw new IllegalStateException("Snapshot already exists for last version");
        }

        Snapshot snapshot = Snapshot.builder()
                .problem(version.getProblem())
                .problemVersion(version)
                .isCommitted(false)
                .build();
        snapshotRepository.save(snapshot);
    }

    @Override
    public List<SnapshotResponse> getAllSnapshots(UUID problemId) {
        return snapshotRepository.findAllByProblem_Id(problemId).stream()
                .map(s -> SnapshotResponse.builder()
                        .snapshotId(s.getId())
                        .problemId(s.getProblem().getId())
                        .versionId(s.getProblemVersion().getId())
                        .versionNumber(s.getProblemVersion().getVersion())
                        .isCommitted(s.getIsCommitted())
                        .build())
                .toList();
    }

    @Override
    public void commitChanges(UUID problemId) {
        Snapshot snapshot = snapshotRepository.findByProblem_Id(
                problemId,
                Sort.by(Sort.Direction.DESC, "problemVersion.version"),
                Limit.of(1)
        );
        if (snapshot == null) {
            throw new IllegalArgumentException("No snapshot found for problem: " + problemId);
        }
        if (Boolean.TRUE.equals(snapshot.getIsCommitted())) {
            throw new IllegalStateException("Latest snapshot is already committed");
        }
        snapshot.setIsCommitted(true);
        snapshotRepository.save(snapshot);
    }
}
