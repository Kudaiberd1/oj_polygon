package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Optional<Problem> findById(String id);

    List<Problem> findAllByOwnerId(UUID ownerId);
}
