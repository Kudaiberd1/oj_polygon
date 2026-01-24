package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Problem;
import com.polygon.onlinejudge.entities.ProblemVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemVersionRepository extends JpaRepository<ProblemVersion, UUID> {

    Optional<ProblemVersion> findFirstByProblem_IdOrderByVersionDesc(UUID problemId);

    int countByProblem(Problem problem);

    Optional<List<ProblemVersion>> findAllByProblem_Id(UUID problemId);
}
