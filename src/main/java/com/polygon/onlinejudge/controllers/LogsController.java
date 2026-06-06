package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.logs.AuthorSolutionLogsResponse;
import com.polygon.onlinejudge.dto.logs.CompletionStatusResponse;
import com.polygon.onlinejudge.dto.logs.LogsResponse;
import com.polygon.onlinejudge.facade.AuthFacade;
import com.polygon.onlinejudge.services.LogsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon/logs")
public class LogsController {
    private final LogsService logsService;
    private final AuthFacade authFacade;

    @GetMapping("/")
    public ResponseEntity<List<LogsResponse>> getLogs() {
        return ResponseEntity.ok(logsService.getAllLogs());
    }

    @GetMapping("/author-solution/{problemVersionId}")
    public ResponseEntity<List<AuthorSolutionLogsResponse>> getAuthorSolutionLogs(@PathVariable UUID problemVersionId) {
return ResponseEntity.ok(logsService.getAuthorSolutionLogs(problemVersionId, authFacade.getEmail()));
    }

    @GetMapping("/completion-status/{versionId}")
    public ResponseEntity<CompletionStatusResponse> getCompletionStatus(@PathVariable UUID versionId) {
        return ResponseEntity.ok(logsService.getCompletionStatus(versionId, authFacade.getEmail()));
    }
}
