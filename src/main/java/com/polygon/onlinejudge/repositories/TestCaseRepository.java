package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    Optional<TestCase> findByGroup_IdAndId(UUID groupId, Long id);

    int countTestCasesByGroup_Id(UUID groupId);

    List<TestCase> findTestCasesByGroup_Id(UUID groupId);
}
