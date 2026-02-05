package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.test.TestCaseRequest;
import com.polygon.onlinejudge.dto.test.TestGroupRequest;
import com.polygon.onlinejudge.dto.test.TestGroupResponse;
import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.TestCase;
import com.polygon.onlinejudge.entities.TestGroup;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.mappers.TestGroupMapper;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import com.polygon.onlinejudge.repositories.TestCaseRepository;
import com.polygon.onlinejudge.repositories.TestGroupRepository;
import com.polygon.onlinejudge.services.ProblemTestService;
import com.polygon.onlinejudge.services.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemTestServiceImpl implements ProblemTestService {

    private final ProblemVersionRepository versionRepository;
    private final TestGroupRepository testGroupRepository;
    private final TestGroupMapper testGroupMapper;
    private final S3Service s3Service;
    private final TestCaseRepository testCaseRepository;

    @Override
    public void setProblemScore(TestGroupRequest request, UUID versionId) {
        ProblemVersion version = versionRepository.findById(versionId).orElseThrow(() -> new IllegalArgumentException("Version not found"));

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

        if (group.getVersion().getStatus() != Status.DRAFT) {
            throw new IllegalStateException("Cannot add tests to non-DRAFT version");
        }

        if (req.getInputPath() == null || req.getInputPath().isBlank()) {
            throw new IllegalArgumentException("Input is empty");
        }

        int nextOrder = testCaseRepository.countTestCasesByGroup_Id(group.getId());

        String baseKey = String.format(
                "polygon/%03d",
                nextOrder
        );

        String inputKey = baseKey + ".in";

        String url = s3Service.putText(inputKey, req.getInputPath());

        TestCase tc = TestCase.builder()
                .group(group)
                .orderId(Long.valueOf(nextOrder))
                .inputPath(url)
                .build();

        testCaseRepository.save(tc);
    }



    @Override
    public void deleteTest(UUID testGroupId, Long id) {
        TestCase testCase = testCaseRepository.findByGroup_IdAndId(testGroupId, id).orElseThrow(() -> new IllegalArgumentException("TestCase not found"));

        testCaseRepository.delete(testCase);
    }
}
