package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.TestGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestGroupRepository extends JpaRepository<TestGroup, UUID> {
    List<TestGroup> findAllByVersion_Id(UUID versionId);
}
