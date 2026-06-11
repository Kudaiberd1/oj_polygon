package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.SnapshotTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SnapshotTestCaseRepository extends JpaRepository<SnapshotTestCase, UUID> {
    List<SnapshotTestCase> findAllBySnapshotId(UUID snapshotId);

    void deleteAllBySnapshotId(UUID snapshotId);
}
