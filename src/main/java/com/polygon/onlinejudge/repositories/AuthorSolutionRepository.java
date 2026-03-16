package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.AuthorSolution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorSolutionRepository extends JpaRepository<AuthorSolution, UUID> {

    Optional<AuthorSolution> findByVersion_Id(UUID versionId);
}
