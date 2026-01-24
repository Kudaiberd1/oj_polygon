package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.problem.ProblemVersionResponse;
import com.polygon.onlinejudge.entities.ProblemVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProblemVersionMapper {

    @Mapping(target = "problemId", source = "problemVersion.problem.id")
    ProblemVersionResponse toDto(ProblemVersion problemVersion);
}
