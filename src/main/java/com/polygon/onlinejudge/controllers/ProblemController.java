package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.dto.problem.ProblemVersionResponse;
import com.polygon.onlinejudge.services.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping()
    public ResponseEntity<List<ProblemResponse>> getAllProblems(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ResponseEntity.ok(problemService.getAllProblems(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblemsById(@PathVariable UUID id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ResponseEntity.ok(problemService.getById(id, email));
    }

    @PostMapping()
    public ResponseEntity<ProblemResponse> createProblem(@RequestBody ProblemRequest problemRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ResponseEntity.ok(problemService.createProblem(email, problemRequest));
    }

    @GetMapping("/{problemId}/versions")
    public ResponseEntity<List<ProblemVersionResponse>> getProblemVersions(@PathVariable UUID problemId){
        List<ProblemVersionResponse> verions = problemService.getProblemVersions(problemId);

        return ResponseEntity.ok(verions);
    }
}
