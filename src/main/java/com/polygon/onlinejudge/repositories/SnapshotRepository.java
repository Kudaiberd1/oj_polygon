package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SnapshotRepository extends JpaRepository<Snapshot, UUID> {
    Snapshot findByProblemIdAndSourceVersionId(UUID problemId, UUID sourceVersionId);

    List<Snapshot> findAllByProblemId(UUID problemId);

    Optional<Snapshot> findFirstByProblemIdOrderBySourceVersionNumberDesc(UUID problemId);
}
