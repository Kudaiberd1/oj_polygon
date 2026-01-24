package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.problem.ProblemRequest;
import com.polygon.onlinejudge.dto.problem.ProblemResponse;
import com.polygon.onlinejudge.services.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon/problem")
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping()
    public ResponseEntity<List<ProblemResponse>> getAllProblems(){
        return ResponseEntity.ok(problemService.getAllproblems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblemsById(@PathVariable String id){
        return ResponseEntity.ok(problemService.getById(id));
    }

    @PostMapping()
    public ResponseEntity<ProblemResponse> createProblem(@RequestBody ProblemRequest problem){
        ProblemResponse newProblem = problemService.createProblem(problem);

        return ResponseEntity.ok(newProblem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProblem(@RequestBody ProblemRequest problem, @PathVariable String id){
        problemService.updateProblem(problem, id);

        return ResponseEntity.ok().build();
    }

}
