package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionRequest;
import com.polygon.onlinejudge.dto.problemVersion.ProblemVersionResponse;
import com.polygon.onlinejudge.entities.ProblemVersion;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = { ProblemStatementMapper.class }
)
public interface ProblemVersionMapper {

    @Mapping(target = "problemId", source = "problemVersion.problem.id")
    @Mapping(target = "statement", source = "problemStatement")
    ProblemVersionResponse toDto(ProblemVersion problemVersion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProblem(ProblemVersionRequest dto, @MappingTarget ProblemVersion problem);
}
