package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Generator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeneratorRepository extends JpaRepository<Generator, UUID> {
    List<Generator> findAllByVersion_Id(UUID versionId);
    Optional<Generator> findByVersion_IdAndName(UUID versionId, String name);
    void deleteAllByVersion_Id(UUID versionId);
}
