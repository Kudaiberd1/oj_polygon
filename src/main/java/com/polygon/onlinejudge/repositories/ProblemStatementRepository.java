package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.ProblemStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProblemStatementRepository extends JpaRepository<ProblemStatement, UUID> {
    Optional<ProblemStatement> findProblemStatementByVersion_Id(UUID versionId);
}
