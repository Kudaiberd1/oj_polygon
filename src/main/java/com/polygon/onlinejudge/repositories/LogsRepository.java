package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogsRepository extends JpaRepository<Logs, UUID> {
}
