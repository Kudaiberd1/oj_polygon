package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.problem.ProblemStatementResponse;
import com.polygon.onlinejudge.entities.ProblemStatement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProblemStatementMapper {

    @Mapping(target = "problemVersionId", source = "entity.version.id")
    ProblemStatementResponse toDto(ProblemStatement entity);
}
