package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.logs.AuthorSolutionLogsResponse;
import com.polygon.onlinejudge.dto.logs.LogsResponse;
import com.polygon.onlinejudge.entities.Logs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogsMapper {

    @Mapping(target = "problemId", source = "logs.version.problem.id")
    @Mapping(target = "versionId", source = "logs.version.id")
    LogsResponse toDto(Logs logs);

    @Mapping(target = "testCaseId", source = "logs.testCaseId")
    @Mapping(target = "orderId", source = "logs.orderId")
    @Mapping(target = "groupId", source = "logs.testGroupId")
    @Mapping(target = "status", source = "logs.status")
    @Mapping(target = "time", expression = "java(logs.getTime() != null && !logs.getTime().isEmpty() ? logs.getTime() + \" ms\" : null)")
    @Mapping(target = "memory", expression = "java(logs.getMemory() != null ? logs.getMemory()/1024 : null)")
    @Mapping(target = "message", source = "logs.message")
    @Mapping(target = "log", source = "logs.log")
    AuthorSolutionLogsResponse toAuthorSolutionLogsDto(Logs logs);
}
