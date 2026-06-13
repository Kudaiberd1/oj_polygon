package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.dto.judge.Judge0SubmissionRequest;
import com.polygon.onlinejudge.dto.judge.Judge0SubmissionResponse;
import com.polygon.onlinejudge.services.Judge0ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class Judge0ClientServiceImpl implements Judge0ClientService {

    private final WebClient judge0WebClient;

    @Override
    public Judge0SubmissionResponse runSubmission(Judge0SubmissionRequest request) {
        Judge0SubmissionRequest encoded = Judge0SubmissionRequest.builder()
                .source_code(encode(request.getSource_code()))
                .stdin(encode(request.getStdin()))
                .language_id(request.getLanguage_id())
                .command_line_arguments(request.getCommand_line_arguments())
                .additional_files(request.getAdditional_files())
                .cpu_time_limit(request.getCpu_time_limit())
                .wall_time_limit(request.getWall_time_limit())
                .memory_limit(request.getMemory_limit())
                .build();

        Judge0SubmissionResponse response = judge0WebClient.post()
                .uri("/submissions?wait=true&base64_encoded=true")
                .bodyValue(encoded)
                .retrieve()
                .bodyToMono(Judge0SubmissionResponse.class)
                .block();

        if (response != null) {
            response.setStdout(decode(response.getStdout()));
            response.setStderr(decode(response.getStderr()));
            response.setCompile_output(decode(response.getCompile_output()));
            response.setMessage(decode(response.getMessage()));
        }
        return response;
    }

    private String encode(String value) {
        if (value == null) return null;
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    private String decode(String value) {
        if (value == null) return null;
        return new String(Base64.getMimeDecoder().decode(value));
    }
}
