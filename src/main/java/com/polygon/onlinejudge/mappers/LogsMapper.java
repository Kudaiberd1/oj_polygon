package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.logs.LogsResponse;
import com.polygon.onlinejudge.entities.Logs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogsMapper {

    @Mapping(target = "problemId", source = "logs.version.problem.id")
    @Mapping(target = "versionId", source = "logs.version.id")
    LogsResponse toDto(Logs logs);
}
