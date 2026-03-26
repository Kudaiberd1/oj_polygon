package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.pagination.PaginatedResponse;
import com.polygon.onlinejudge.dto.pagination.PaginationParams;
import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.facade.AuthFacade;
import com.polygon.onlinejudge.services.ProblemService;
import com.polygon.onlinejudge.services.SnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final SnapshotService snapshotService;
    private final AuthFacade authFacade;

    @GetMapping()
    public ResponseEntity<PaginatedResponse<ProblemResponse>> getAllProblems(@ParameterObject @ModelAttribute PaginationParams paginationParams){
        var problems = problemService.getAllProblems(authFacade.getEmail(), paginationParams);

        return ResponseEntity.ok(new PaginatedResponse<>(problems));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblemsById(@PathVariable UUID id){
        return ResponseEntity.ok(problemService.getById(id, authFacade.getEmail()));
    }

    @PostMapping()
    public ResponseEntity<ProblemResponse> createProblem(@RequestBody ProblemRequest problemRequest){
        return ResponseEntity.ok(problemService.createProblem(authFacade.getEmail(), problemRequest));
    }

    @GetMapping("/{problemId}/versions")
    public ResponseEntity<List<ProblemVersionResponse>> getProblemVersions(@PathVariable UUID problemId){
        List<ProblemVersionResponse> versions = problemService.getProblemVersions(problemId);

        return ResponseEntity.ok(versions);
    }

    @PatchMapping("/{problemId}/snapshot")
    public ResponseEntity<Void> updateSnapshot(@PathVariable UUID problemId){
        snapshotService.updateSnapshot(problemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{problemId}/snapshot")
    public ResponseEntity<UUID> getSnapshot(@PathVariable UUID problemId){
        return ResponseEntity.ok(snapshotService.getSnapshot(problemId));
    }
}
