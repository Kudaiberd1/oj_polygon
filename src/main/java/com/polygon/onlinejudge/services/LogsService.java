package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.logs.AuthorSolutionLogsResponse;
import com.polygon.onlinejudge.dto.logs.CompletionStatusResponse;
import com.polygon.onlinejudge.dto.logs.LogsResponse;
import com.polygon.onlinejudge.entities.Logs;
import java.util.UUID;
import java.util.List;

public interface LogsService {
    void saveLog(Logs log);

    List<LogsResponse> getAllLogs();

    List<AuthorSolutionLogsResponse> getAuthorSolutionLogs(UUID problemVersionId, String email);

    CompletionStatusResponse getCompletionStatus(UUID versionId, String email);

    void clearValidationLogs(UUID versionId);
}
