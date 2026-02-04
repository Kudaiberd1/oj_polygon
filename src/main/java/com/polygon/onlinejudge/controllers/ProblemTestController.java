package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.dto.test.TestGroupRequest;
import com.polygon.onlinejudge.dto.test.TestGroupResponse;
import com.polygon.onlinejudge.services.ProblemTestService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/polygon/test-groups")
public class ProblemTestController {

    private final ProblemTestService problemTestService;

    @PostMapping("/version/{versionId}")
    public ResponseEntity<Void> setProblemScore(@RequestBody TestGroupRequest request, @PathVariable("versionId") UUID versionId) {
        problemTestService.setProblemScore(request, versionId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/version/{versionId}")
    public ResponseEntity<List<TestGroupResponse>> getAllTestGroups(@PathVariable("versionId") UUID versionId) {
        List<TestGroupResponse> testGroups = problemTestService.getAllTestGroups(versionId);
        return ResponseEntity.ok(testGroups);
    }

    @PostMapping(
            value = "/{testGroupId}/tests",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> uploadTestCase(
            @PathVariable UUID testGroupId,
            @RequestPart("input") MultipartFile inputFile
    ) {
        problemTestService.createTestCase(
                testGroupId,
                inputFile
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{testGroupId}/tests")
    public ResponseEntity<Void> deleteTestCase(@PathVariable UUID testGroupId, @RequestParam Long id) {
        problemTestService.deleteTest(testGroupId, id);
        return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).build();
    }
}
