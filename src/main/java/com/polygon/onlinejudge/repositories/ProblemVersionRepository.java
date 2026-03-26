package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemVersionRepository extends JpaRepository<ProblemVersion, UUID> {

    interface ProblemVersionView {
        UUID getId();
        Status getStatus();
        Integer getVersion();
    }

    Optional<ProblemVersion> findFirstByProblem_IdOrderByVersionDesc(UUID problemId);

    @Query("""
       SELECT pv.id AS id, pv.version AS version, pv.status AS status
       FROM ProblemVersion pv
       WHERE pv.problem.id = :problemId
       ORDER BY pv.version DESC
       LIMIT 1
       """)
    ProblemVersionView findLastVersion(UUID problemId);

    List<ProblemVersion> findAllByProblem_Id(UUID problemId);
}
