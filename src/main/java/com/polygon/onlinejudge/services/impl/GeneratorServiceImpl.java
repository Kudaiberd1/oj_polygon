package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.generator.*;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionRequest;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;
import com.polygon.onlinejudge.entities.*;
import com.polygon.onlinejudge.entities.enums.Language;
import com.polygon.onlinejudge.policy.ProblemVersionPolicy;
import com.polygon.onlinejudge.providers.TestlibProvider;
import com.polygon.onlinejudge.repositories.*;
import com.polygon.onlinejudge.services.GeneratorService;
import com.polygon.onlinejudge.services.Judge0ClientService;
import com.polygon.onlinejudge.services.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratorServiceImpl implements GeneratorService {

    private final ProblemVersionRepository versionRepository;
    private final GeneratorRepository generatorRepository;
    private final TestGroupRepository testGroupRepository;
    private final TestCaseRepository testCaseRepository;
    private final S3Service s3Service;
    private final Judge0ClientService judge0ClientService;
    private final TestlibProvider testlibProvider;
    private final ProblemVersionPolicy problemVersionPolicy;

    @Override
    public GeneratorResponse createGenerator(UUID versionId, GeneratorRequest request) {
        ProblemVersion version = loadVersion(versionId);
        if (problemVersionPolicy.checkVersion(version)) {
            throw new IllegalStateException("Cannot modify a VERIFIED version");
        }

        Language language = Language.valueOf(request.getLanguage());
        String key = buildGeneratorKey(version, request.getName(), language);
        s3Service.putText(key, request.getSourceCode());

        Generator generator = Generator.builder()
                .version(version)
                .name(request.getName())
                .sourceCodeKey(key)
                .language(language)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toResponse(generatorRepository.save(generator));
    }

    @Override
    public List<GeneratorResponse> getGenerators(UUID versionId) {
        return generatorRepository.findAllByVersion_IdOrderByCreatedAtAsc(versionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public GeneratorResponse updateGenerator(UUID generatorId, GeneratorRequest request) {
        Generator generator = generatorRepository.findById(generatorId)
                .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + generatorId));

        ProblemVersion version = generator.getVersion();
        if (problemVersionPolicy.checkVersion(version)) {
            throw new IllegalStateException("Cannot modify a VERIFIED version");
        }

        s3Service.delete(generator.getSourceCodeKey());

        Language language = Language.valueOf(request.getLanguage());
        String newKey = buildGeneratorKey(version, request.getName(), language);
        s3Service.putText(newKey, request.getSourceCode());

        generator.setName(request.getName());
        generator.setSourceCodeKey(newKey);
        generator.setLanguage(language);
        generator.setUpdatedAt(LocalDateTime.now());

        return toResponse(generatorRepository.save(generator));
    }

    @Override
    public void deleteGenerator(UUID generatorId) {
        Generator generator = generatorRepository.findById(generatorId)
                .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + generatorId));
        s3Service.delete(generator.getSourceCodeKey());
        generatorRepository.delete(generator);
    }

    @Override
    public ScriptRunResponse runScript(UUID versionId, ScriptRunRequest request) {
        ProblemVersion version = loadVersion(versionId);

        TestGroup testGroup = testGroupRepository.findById(request.getTestGroupId())
                .orElseThrow(() -> new IllegalArgumentException("TestGroup not found: " + request.getTestGroupId()));

        List<ScriptLineResult> results = new ArrayList<>();

        for (String rawLine : request.getScript().split("\n")) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            results.add(executeLine(line, versionId, version, testGroup));
        }

        int successCount = (int) results.stream().filter(ScriptLineResult::isSuccess).count();

        return ScriptRunResponse.builder()
                .results(results)
                .totalLines(results.size())
                .successCount(successCount)
                .failCount(results.size() - successCount)
                .build();
    }

    private ScriptLineResult executeLine(String line, UUID versionId, ProblemVersion version, TestGroup testGroup) {
        String[] parts = line.split(">");
        if (parts.length != 2) {
            return fail(line, "Invalid format — expected 'generatorName [args] > $'");
        }

        String command = parts[0].trim();
        String target = parts[1].trim();

        if (!target.equals("$")) {
            return fail(line, "Only '$' target is supported");
        }

        String[] tokens = command.split("\\s+");
        String generatorName = tokens[0];
        String args = tokens.length > 1
                ? String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length))
                : "";

        Generator generator = generatorRepository.findByVersion_IdAndName(versionId, generatorName).orElse(null);
        if (generator == null) {
            return fail(line, "Generator '" + generatorName + "' not found");
        }

        String sourceCode = s3Service.getText(generator.getSourceCodeKey());
        Language lang = generator.getLanguage();

        int langId = switch (lang) {
            case CPP -> 54;
            case JAVA -> 62;
            case PY -> 71;
        };

        double cpuTimeLimit = switch (lang) {
            case CPP -> 10.0;
            case JAVA, PY -> 15.0;
        };

        Judge0SubmissionRequest.Judge0SubmissionRequestBuilder judgeBuilder = Judge0SubmissionRequest.builder()
                .source_code(sourceCode)
                .language_id(langId)
                .stdin(args)
                .command_line_arguments(args)
                .cpu_time_limit(cpuTimeLimit)
                .wall_time_limit(cpuTimeLimit + 5.0)
                .memory_limit(262144L);

        if (lang == Language.CPP) {
            judgeBuilder.additional_files(testlibProvider.getBase64Zip());
        }

        Judge0SubmissionResponse response;
        try {
            response = judge0ClientService.runSubmission(judgeBuilder.build());
        } catch (Exception e) {
            return fail(line, "Judge0 error: " + e.getMessage());
        }

        if (response == null) {
            return fail(line, "Judge0 returned null");
        }

        if (response.getStatus() == null || response.getStatus().getId() != 3) {
            String error = response.getStderr() != null ? response.getStderr()
                    : response.getCompile_output() != null ? response.getCompile_output()
                    : response.getMessage() != null ? response.getMessage()
                    : "Unknown error";
            return fail(line, error);
        }

        String generatedInput = response.getStdout();
        if (generatedInput == null || generatedInput.isBlank()) {
            return fail(line, "Generator produced no output");
        }

        long nextOrderId = testCaseRepository.findMaxOrderIdByGroupId(testGroup.getId()) + 1;

        String inputKey = String.format(
                "problems/%s/versions/%s/tests/%s/%03d.in",
                version.getProblem().getId(),
                version.getId(),
                testGroup.getId(),
                nextOrderId
        );

        s3Service.putText(inputKey, generatedInput);

        testCaseRepository.save(TestCase.builder()
                .group(testGroup)
                .problemVersion(version)
                .orderId(nextOrderId)
                .inputPath(inputKey)
                .build());

        return ScriptLineResult.builder()
                .line(line)
                .success(true)
                .output(generatedInput.substring(0, Math.min(100, generatedInput.length())))
                .testOrderId((int) nextOrderId)
                .build();
    }

    private ScriptLineResult fail(String line, String error) {
        return ScriptLineResult.builder().line(line).success(false).error(error).build();
    }

    private ProblemVersion loadVersion(UUID versionId) {
        return versionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
    }

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");

    private String buildGeneratorKey(ProblemVersion version, String name, Language language) {
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid generator name: must match ^[A-Za-z0-9_-]{1,64}$");
        }
        String ext = switch (language) {
            case JAVA -> "java";
            case CPP -> "cpp";
            case PY -> "py";
        };
        return String.format("problems/%s/versions/%s/generators/%s.%s",
                version.getProblem().getId(), version.getId(), name, ext);
    }

    private GeneratorResponse toResponse(Generator g) {
        return GeneratorResponse.builder()
                .id(g.getId())
                .versionId(g.getVersion().getId())
                .name(g.getName())
                .sourceCodeKey(g.getSourceCodeKey())
                .language(g.getLanguage() != null ? g.getLanguage().name() : null)
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .build();
    }
}
