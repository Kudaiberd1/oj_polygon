package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.test.TestCaseRequest;
import com.polygon.onlinejudge.dto.test.TestGroupRequest;
import com.polygon.onlinejudge.dto.test.TestGroupResponse;

import java.util.List;
import java.util.UUID;

public interface ProblemTestService {

    void setProblemScore(TestGroupRequest request, UUID versionId);

    List<TestGroupResponse> getAllTestGroups(UUID versionId);

    void createTestCase(UUID testGroupId, TestCaseRequest req);

    void deleteTest(UUID testGroupId, Long id);
}
