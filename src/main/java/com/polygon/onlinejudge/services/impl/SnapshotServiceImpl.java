package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.entities.Snapshot;
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
    public UUID getSnapshot(UUID problemId) {
        Snapshot snapshot = snapshotRepository.getSnapshotByProblemId(problemId);
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot not found");
        }
        return snapshot.getProblemId();
    }

    @Override
    public void updateSnapshot(UUID problemId) {
        Snapshot snapshot = snapshotRepository.getSnapshotByProblemId(problemId);
        if (snapshot == null) {
            var problemVersion = problemVersionRepository.findLastVersion(problemId);

            if (problemVersion.getStatus() != Status.VERIFIED) {
                throw new IllegalStateException("Last verified problem version is not verified");
            }
            Snapshot newSnapshot = Snapshot.builder()
                    .problemId(problemId)
                    .problemVersionId(problemVersion.getId())
                    .build();
            snapshotRepository.save(newSnapshot);
        }else{
            var problemVersion = problemVersionRepository.findLastVersion(problemId);

            if (problemVersion.getStatus() != Status.VERIFIED) {
                throw new IllegalStateException("Last verified problem version is not verified");
            }

            snapshot.setProblemVersionId(problemVersion.getId());
            snapshotRepository.save(snapshot);
        }
    }
}
