package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.entities.ProblemVersion;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProblemVersionMapper {

    @Mapping(target = "problemId", source = "problemVersion.problem.id")
    @Mapping(target = "statement.id", source = "problemVersion.problemStatement.id")
    @Mapping(target = "statement.description", source = "problemVersion.problemStatement.description")
    @Mapping(target = "statement.inputDescription", source = "problemVersion.problemStatement.inputDescription")
    @Mapping(target = "statement.outputDescription", source = "problemVersion.problemStatement.outputDescription")
    @Mapping(target = "statement.notes", source = "problemVersion.problemStatement.notes")
    ProblemVersionResponse toDto(ProblemVersion problemVersion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProblem(ProblemVersionRequest dto, @MappingTarget ProblemVersion problem);
}
