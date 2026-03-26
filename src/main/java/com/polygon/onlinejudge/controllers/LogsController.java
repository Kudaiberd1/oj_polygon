package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.logs.LogsResponse;
import com.polygon.onlinejudge.mappers.LogsMapper;
import com.polygon.onlinejudge.repositories.LogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon/logs")
public class LogsController {
    private final LogsRepository logsRepository;
    private final LogsMapper logsMapper;

    @GetMapping("/")
    public ResponseEntity<List<LogsResponse>> getLogs() {
        return ResponseEntity.ok(logsRepository.findAll().stream().map(logsMapper::toDto).toList());
    }
}
