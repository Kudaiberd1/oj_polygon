package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findById(String id);

}
