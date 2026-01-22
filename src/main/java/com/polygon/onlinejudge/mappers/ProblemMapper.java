package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.problem.ProblemRequestDto;
import com.polygon.onlinejudge.dto.problem.ProblemResponseDto;
import com.polygon.onlinejudge.entities.Problem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    ProblemResponseDto toDto(Problem problem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProblem(ProblemRequestDto dto, @MappingTarget Problem problem);
}
