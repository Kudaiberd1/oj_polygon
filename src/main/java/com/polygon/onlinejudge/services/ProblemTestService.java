package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.test.ExampleTestCaseRequest;
import com.polygon.onlinejudge.dto.test.TestCaseRequest;
import com.polygon.onlinejudge.dto.test.TestGroupRequest;
import com.polygon.onlinejudge.dto.test.TestGroupResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProblemTestService {

    void createTestGroup(TestGroupRequest request, UUID versionId);

    List<TestGroupResponse> getAllTestGroups(UUID versionId);

    void createTestCase(UUID testGroupId, TestCaseRequest req);

    void deleteTest(UUID testGroupId, Long id);

    void deleteTestGroup(UUID testGroupId);

    void makeExampleTestCase(UUID testGroupId, Long testCaseId, ExampleTestCaseRequest request);

    void updateGroupScore(UUID testGroupId, Integer score);
}
