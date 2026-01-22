package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.problem.ProblemResponseDto;
import com.polygon.onlinejudge.services.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon/problem")
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping()
    public ResponseEntity<List<ProblemResponseDto>> getAllProblems(){
        return ResponseEntity.ok(problemService.getAllproblems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponseDto> getProblemsById(@PathVariable String id){
        return ResponseEntity.ok(problemService.getById(id));
    }
}
