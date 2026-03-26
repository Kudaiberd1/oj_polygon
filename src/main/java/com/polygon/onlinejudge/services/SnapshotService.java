package com.polygon.onlinejudge.services;

import java.util.UUID;

public interface SnapshotService {
    UUID  getSnapshot(UUID problemId);

    void updateSnapshot(UUID problemId);
}
