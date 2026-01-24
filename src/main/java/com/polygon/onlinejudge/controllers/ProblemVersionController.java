package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.problem.ProblemStatementRequest;
import com.polygon.onlinejudge.dto.problem.ProblemStatementResponse;
import com.polygon.onlinejudge.dto.problem.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problem.ProblemVersionResponse;
import com.polygon.onlinejudge.services.ProblemVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon")
public class ProblemVersionController {

    private final ProblemVersionService problemVersionService;

    @PostMapping("/problems/{problemId}/versions")
    public ResponseEntity<ProblemVersionResponse> createVersion(@PathVariable UUID problemId, @RequestBody ProblemVersionRequest request) {
        ProblemVersionResponse response = problemVersionService.createVersion(problemId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/versions/{versionId}")
    public ResponseEntity<ProblemVersionResponse> getVersion(@PathVariable UUID versionId) {
        ProblemVersionResponse response = problemVersionService.getVersion(versionId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/versions/{versionId}/statement")
    public ResponseEntity<ProblemStatementResponse> updateStatement(@PathVariable UUID versionId, @RequestBody ProblemStatementRequest request) {
        ProblemStatementResponse response = problemVersionService.updateStatement(versionId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/versions/{versionId}/statement")
    public ResponseEntity<ProblemStatementResponse> getStatement(@PathVariable UUID versionId) {
        ProblemStatementResponse response = problemVersionService.getStatement(versionId);

        return ResponseEntity.ok(response);
    }
}
