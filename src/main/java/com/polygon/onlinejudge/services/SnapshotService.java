package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.snapshot.SnapshotResponse;

import java.util.List;
import java.util.UUID;

public interface SnapshotService {
    UUID getSnapshot(UUID problemId, UUID versionId);

    void createSnapshot(UUID problemId);

    List<SnapshotResponse> getAllSnapshots(UUID problemId);

    void commitChanges(UUID problemId);
}
