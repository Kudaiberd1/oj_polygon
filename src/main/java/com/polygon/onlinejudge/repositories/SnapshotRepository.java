package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Snapshot;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SnapshotRepository extends JpaRepository<Snapshot, UUID> {
    Snapshot findByProblem_IdAndProblemVersion_Id(UUID problemId, UUID problemVersionId);

    List<Snapshot> findAllByProblem_Id(UUID problemId);

    Snapshot findByProblem_Id(UUID problemId, Sort sort, Limit limit);
}
