package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.generator.*;
import com.polygon.onlinejudge.services.GeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/polygon")
@RequiredArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;

    @PostMapping("/versions/{versionId}/generators")
    public ResponseEntity<GeneratorResponse> createGenerator(
            @PathVariable UUID versionId,
            @RequestBody GeneratorRequest request) {
        return ResponseEntity.ok(generatorService.createGenerator(versionId, request));
    }

    @GetMapping("/versions/{versionId}/generators")
    public ResponseEntity<List<GeneratorResponse>> getGenerators(@PathVariable UUID versionId) {
        return ResponseEntity.ok(generatorService.getGenerators(versionId));
    }

    @PutMapping("/generators/{generatorId}")
    public ResponseEntity<GeneratorResponse> updateGenerator(
            @PathVariable UUID generatorId,
            @RequestBody GeneratorRequest request) {
        return ResponseEntity.ok(generatorService.updateGenerator(generatorId, request));
    }

    @DeleteMapping("/generators/{generatorId}")
    public ResponseEntity<Void> deleteGenerator(@PathVariable UUID generatorId) {
        generatorService.deleteGenerator(generatorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/versions/{versionId}/script/run")
    public ResponseEntity<ScriptRunResponse> runScript(
            @PathVariable UUID versionId,
            @RequestBody ScriptRunRequest request) {
        return ResponseEntity.ok(generatorService.runScript(versionId, request));
    }
}
