package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.generator.*;

import java.util.List;
import java.util.UUID;

public interface GeneratorService {
    GeneratorResponse createGenerator(UUID versionId, GeneratorRequest request);
    List<GeneratorResponse> getGenerators(UUID versionId);
    GeneratorResponse updateGenerator(UUID generatorId, GeneratorRequest request);
    void deleteGenerator(UUID generatorId);
    ScriptRunResponse runScript(UUID versionId, ScriptRunRequest request);
}
