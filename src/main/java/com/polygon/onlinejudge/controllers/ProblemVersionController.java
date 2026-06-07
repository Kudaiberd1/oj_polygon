package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;
import com.polygon.onlinejudge.dto.problem.AuthorSolutionRequest;
import com.polygon.onlinejudge.dto.problem.AuthorSolutionResponse;
import com.polygon.onlinejudge.dto.problemVersion.BranchVersionResponse;
import com.polygon.onlinejudge.dto.problemVersion.OpenVersionResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemStatementResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.dto.snapshot.SnapshotResponse;
import com.polygon.onlinejudge.dto.test.TestCaseResponse;
import com.polygon.onlinejudge.services.ProblemVersionService;
import com.polygon.onlinejudge.services.SnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon")
public class ProblemVersionController {

    private final ProblemVersionService problemVersionService;
    private final SnapshotService snapshotService;

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

    @GetMapping("/versions/{versionId}/solution")
    public ResponseEntity<AuthorSolutionResponse> getAuthorSolution(@PathVariable UUID versionId) {
        AuthorSolutionResponse response = problemVersionService.getAuthorSolution(versionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/versions/{versionId}/solution")
    public ResponseEntity<AuthorSolutionResponse> updateAuthorSolution(@PathVariable UUID versionId, @RequestBody AuthorSolutionRequest request) {
        problemVersionService.updateAuthorSolution(versionId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/versions/{versionId}/validate-solution")
    public ResponseEntity<Void> checkAuthorSolution(@PathVariable UUID versionId) {
        problemVersionService.checkAuthorSolution(versionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test-code/{solutionId}")
    public ResponseEntity<Judge0SubmissionResponse> testCode(@PathVariable UUID solutionId, @RequestParam String test) {
        return ResponseEntity.ok(problemVersionService.testCode(solutionId, test));
    }

    @PostMapping("/problems/{problemId}/versions/snapshot")
    public ResponseEntity<Void> createSnapshot(@PathVariable UUID problemId) {
        snapshotService.createSnapshot(problemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/problems/{problemId}/snapshots")
    public ResponseEntity<List<SnapshotResponse>> getAllSnapshots(@PathVariable UUID problemId) {
        return ResponseEntity.ok(snapshotService.getAllSnapshots(problemId));
    }

    @PatchMapping("/problems/{problemId}/snapshots/commit-changes")
    public ResponseEntity<Void> commitChanges(@PathVariable UUID problemId) {
        snapshotService.commitChanges(problemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/versions/{versionId}/test-cases")
    public ResponseEntity<List<TestCaseResponse>> getExampleTestCases(@PathVariable UUID versionId) {
        return ResponseEntity.ok(problemVersionService.getExampleTestCases(versionId));
    }

    @PostMapping("/problems/{problemId}/open")
    public ResponseEntity<OpenVersionResponse> openProblem(@PathVariable UUID problemId) {
        return ResponseEntity.ok(problemVersionService.openProblem(problemId));
    }

    @PostMapping("/versions/{versionId}/branch")
    public ResponseEntity<BranchVersionResponse> branchVersion(@PathVariable UUID versionId) {
        return ResponseEntity.ok(problemVersionService.branchVersion(versionId));
    }
}
