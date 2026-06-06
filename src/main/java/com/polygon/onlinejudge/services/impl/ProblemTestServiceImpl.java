package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.test.TestCaseRequest;
import com.polygon.onlinejudge.dto.test.TestGroupRequest;
import com.polygon.onlinejudge.dto.test.TestGroupResponse;
import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.TestCase;
import com.polygon.onlinejudge.entities.TestGroup;
import com.polygon.onlinejudge.mappers.TestGroupMapper;
import com.polygon.onlinejudge.policy.ProblemVersionPolicy;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.repositories.TestCaseRepository;
import com.polygon.onlinejudge.repositories.TestGroupRepository;
import com.polygon.onlinejudge.services.ProblemTestService;
import com.polygon.onlinejudge.services.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemTestServiceImpl implements ProblemTestService {

    private final ProblemVersionRepository versionRepository;
    private final ProblemVersionPolicy problemVersionPolicy;
    private final TestGroupRepository testGroupRepository;
    private final TestGroupMapper testGroupMapper;
    private final S3Service s3Service;
    private final TestCaseRepository testCaseRepository;

    @Override
    public void setProblemScore(TestGroupRequest request, UUID versionId) {
        ProblemVersion version = versionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Version not found"));
        problemVersionPolicy.checkVersion(version);

        TestGroup testGroup = TestGroup.builder()
                .points(request.getPoints())
                .version(version)
                .build();

        testGroupRepository.save(testGroup);
    }

    @Override
    public List<TestGroupResponse> getAllTestGroups(UUID versionId) {
        List<TestGroup> testGroups = testGroupRepository.findAllByVersion_Id(versionId);

        return testGroups.stream().map(testGroupMapper::toDto).toList();
    }

    @Override
    public void createTestCase(UUID testGroupId, TestCaseRequest req) {
        TestGroup group = testGroupRepository.findById(testGroupId)
                .orElseThrow(() -> new IllegalArgumentException("TestGroup not found"));

        ProblemVersion version = group.getVersion();

        if (req.getInput() == null || req.getInput().isBlank()) {
            throw new IllegalArgumentException("Input is empty");
        }

        int nextOrder = testCaseRepository.countTestCasesByGroup_Id(group.getId());

        UUID problemId = group.getVersion().getProblem().getId();
        UUID versionId = group.getVersion().getId();
        String inputKey = String.format(
                "problems/%s/versions/%s/tests/%s/%03d.in",
                problemId,
                versionId,
                group.getId(),
                nextOrder
        );

        String url = s3Service.putText(inputKey, req.getInput());

        TestCase tc = TestCase.builder()
                .group(group)
                .problemVersion(version)
                .orderId((long) nextOrder)
                .inputPath(url)
                .build();

        testCaseRepository.save(tc);
    }


    @Override
    public void deleteTest(UUID testGroupId, Long id) {
        TestCase testCase = testCaseRepository.findByGroup_IdAndId(testGroupId, id).orElseThrow(() -> new IllegalArgumentException("TestCase not found"));
        TestGroup testGroup = testGroupRepository.findById(testGroupId).orElseThrow(() -> new IllegalArgumentException("TestGroup not found"));

        problemVersionPolicy.checkVersion(testGroup.getVersion());

        deleteS3Files(testCase);
        testCaseRepository.delete(testCase);
    }

    @Override
    public void deleteTestGroup(UUID testGroupId) {
        TestGroup testGroup = testGroupRepository.findById(testGroupId).orElseThrow(() -> new IllegalArgumentException("TestGroup not found"));

        problemVersionPolicy.checkVersion(testGroup.getVersion());

        testCaseRepository.findTestCasesByGroup_Id(testGroupId).forEach(this::deleteS3Files);
        testGroupRepository.delete(testGroup);
    }

    private void deleteS3Files(TestCase tc) {
        if (tc.getInputPath() != null) s3Service.delete(tc.getInputPath());
        if (tc.getOutputPath() != null) s3Service.delete(tc.getOutputPath());
    }

    @Override
    public void makeExampleTestCase(UUID testGroupId, Long testCaseId) {
        TestCase testCase = testCaseRepository.findByGroup_IdAndId(testGroupId, testCaseId).orElseThrow(() -> new IllegalArgumentException("TestCase not found"));
        testCase.setIsExample(testCase.getIsExample() == null || !testCase.getIsExample());
        testCaseRepository.save(testCase);
    }
}
