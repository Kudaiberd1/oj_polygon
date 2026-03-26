package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, UUID> {
    Page<Problem> findAllByOwnerId(UUID ownerId, Pageable pageable);
}
