package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LogsRepository extends JpaRepository<Logs, UUID> {

    @Query(value = """
    WITH ranked_logs AS (
        SELECT
            l.*,
            ROW_NUMBER() OVER (
                PARTITION BY l.problem_version_id, l.test_group_id, l.order_id
                ORDER BY l.verified_at DESC
            ) AS rn
        FROM logs l
        WHERE l.problem_version_id = :problemVersionId
    )
    SELECT rl.id, rl.problem_version_id, rl.status, rl.log, rl.message,
           rl.time, rl.memory, rl.order_id, rl.test_group_id, rl.verified_at
    FROM ranked_logs rl
    WHERE rl.rn = 1
    ORDER BY rl.test_group_id, rl.order_id
    """,
            nativeQuery = true)
    List<Logs> findAuthorSolutionLogsByProblemVersionId(
            @Param("problemVersionId") UUID problemVersionId
    );
}
