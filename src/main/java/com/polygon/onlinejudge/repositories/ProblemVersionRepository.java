package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemVersionRepository extends JpaRepository<ProblemVersion, UUID> {

    Optional<ProblemVersion> findFirstByProblem_IdOrderByVersionDesc(UUID problemId);

    @Query("""
       SELECT pv
       FROM ProblemVersion pv
       WHERE pv.problem.id = :problemId AND pv.status = 'VERIFIED'
       ORDER BY pv.version DESC
       LIMIT 1
       """)
    Optional<ProblemVersion> findLastVersion(UUID problemId);

    List<ProblemVersion> findAllByProblem_Id(UUID problemId);

    Optional<ProblemVersion> findTopByProblem_IdAndStatusOrderByVersionDesc(UUID problemId, Status status);

    boolean existsByProblem_IdAndStatus(UUID problemId, Status status);

    int countByProblem_Id(UUID problemId);
}
