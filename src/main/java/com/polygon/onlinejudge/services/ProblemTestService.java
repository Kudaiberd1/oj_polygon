package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.test.TestGroupRequest;
import com.polygon.onlinejudge.dto.test.TestGroupResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProblemTestService {

    void setProblemScore(TestGroupRequest request, UUID versionId);

    List<TestGroupResponse> getAllTestGroups(UUID versionId);

    void createTestCase(UUID testGroupId, MultipartFile inputFile);

    void deleteTest(UUID testGroupId, Long id);
}
