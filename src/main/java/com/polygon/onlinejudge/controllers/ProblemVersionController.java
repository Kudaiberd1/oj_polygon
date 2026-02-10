package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.problem.AuthorSolutionRequest;
import com.polygon.onlinejudge.dto.problem.AuthorSolutionResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.services.ProblemVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    @PutMapping("/versions/{versionId}")
    public ResponseEntity<ProblemVersionResponse> updateVersion(@PathVariable UUID versionId, @RequestBody ProblemVersionRequest request) {
        ProblemVersionResponse response = problemVersionService.updateVersion(versionId, request);

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

    @PostMapping("/versions/{versionId}/finalize")
    public ResponseEntity<Map<String, String>> finalizeVersion(@PathVariable UUID versionId) {
        problemVersionService.finalizeVersion(versionId);

        return ResponseEntity.ok(Map.of("message", "Version has been finalized"));
    }

    @PostMapping("/versions/{versionId}/solution")
    public ResponseEntity<AuthorSolutionResponse> addAuthorSolution(@PathVariable UUID versionId, @RequestBody AuthorSolutionRequest request) {
        AuthorSolutionResponse response = problemVersionService.addAuthorSolution(versionId, request);
        return ResponseEntity.ok(response);
    }
}
