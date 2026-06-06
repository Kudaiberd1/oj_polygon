package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.entities.Snapshot;
import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.repositories.SnapshotRepository;
import com.polygon.onlinejudge.services.SnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void createSnapshot(UUID problemId, UUID versionId) {
        ProblemVersion version = problemVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));

        if (!version.getProblem().getId().equals(problemId)) {
            throw new IllegalArgumentException("Version does not belong to problem: " + problemId);
        }

        if (version.getStatus() != Status.VERIFIED) {
            throw new IllegalStateException("Only VERIFIED versions can be snapshotted");
        }

        Snapshot snapshot = Snapshot.builder()
                .problem(version.getProblem())
                .problemVersion(version)
                .build();
        snapshotRepository.save(snapshot);
    }
}
