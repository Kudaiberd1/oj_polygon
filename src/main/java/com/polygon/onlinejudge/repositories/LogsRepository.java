package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LogsRepository extends JpaRepository<Logs, UUID> {

    @Query(value = """
    SELECT
        latest.id                                        AS id,
        CAST(:problemVersionId AS uuid)                  AS problem_version_id,
        tc.order_id                                      AS order_id,
        tg.id                                            AS test_group_id,
        tc.id                                            AS test_case_id,
        latest.status                                    AS status,
        latest.time                                      AS time,
        latest.memory                                    AS memory,
        latest.message                                   AS message,
        latest.log                                       AS log,
        latest.verified_at                               AS verified_at
    FROM test_case tc
    JOIN test_group tg ON tc.test_group_id = tg.id
    JOIN (
        SELECT id, test_group_id, order_id, status, time, memory, message, log, verified_at,
               ROW_NUMBER() OVER (
                   PARTITION BY test_group_id, order_id
                   ORDER BY verified_at DESC
               ) AS rn
        FROM logs
        WHERE problem_version_id = :problemVersionId
    ) latest ON latest.test_group_id = tg.id
            AND latest.order_id = tc.order_id
            AND latest.rn = 1
    WHERE tg.problem_version_id = :problemVersionId
    ORDER BY tg.id, tc.order_id
    """,
            nativeQuery = true)
    List<Logs> findAuthorSolutionLogsByProblemVersionId(
            @Param("problemVersionId") UUID problemVersionId
    );

    void deleteAllByVersion_Id(UUID versionId);
}
