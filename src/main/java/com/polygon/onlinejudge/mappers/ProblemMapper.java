package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.problem.ProblemResponseDto;
import com.polygon.onlinejudge.entities.Problem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    ProblemResponseDto toDto(Problem problem);
}
