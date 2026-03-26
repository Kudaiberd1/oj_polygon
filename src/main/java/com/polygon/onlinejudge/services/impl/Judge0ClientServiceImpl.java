package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.judge.Judge0SubmissionRequest;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;
import com.polygon.onlinejudge.services.Judge0ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class Judge0ClientServiceImpl implements Judge0ClientService {

    private final WebClient judge0WebClient;

    @Override
    public Judge0SubmissionResponse runSubmission(Judge0SubmissionRequest request) {
        return judge0WebClient.post()
                .uri("/submissions?wait=true")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Judge0SubmissionResponse.class)
                .block();
    }

}
