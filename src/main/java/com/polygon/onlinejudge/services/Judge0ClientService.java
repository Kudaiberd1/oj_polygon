package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.dto.judge.Judge0SubmissionRequest;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;

public interface Judge0ClientService {
    Judge0SubmissionResponse runSubmission(Judge0SubmissionRequest request);
}
