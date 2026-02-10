package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.problem.AuthorSolutionResponse;
import com.polygon.onlinejudge.entities.AuthorSolution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorSolutionMapper {

    @Mapping(target = "problemVersionId", source = "solution.version.id")
    AuthorSolutionResponse toDto(AuthorSolution solution);
}
