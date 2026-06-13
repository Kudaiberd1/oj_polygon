package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.GeneratorScript;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GeneratorScriptRepository extends JpaRepository<GeneratorScript, UUID> {
    Optional<GeneratorScript> findByVersion_Id(UUID versionId);
}
