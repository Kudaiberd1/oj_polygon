package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SnapshotRepository extends JpaRepository<Snapshot, UUID> {
    Snapshot getSnapshotByProblemId(UUID problemId);
}
