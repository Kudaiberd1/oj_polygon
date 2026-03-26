package com.polygon.onlinejudge.services.impl;

import com.polygon.onlinejudge.services.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    @Override
    public String putText(String key, String text) {
        if (text == null) text = "";

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain; charset=utf-8")
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(bytes));
        return getFileUrl(key);
    }

    @Override
    public String getText(String key) {
        ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucketName).key(key).build());
        return bytes.asString(StandardCharsets.UTF_8);
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    @Override
    public String getInput(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(normalizeKey(key))
                .build();

        try (InputStream inputStream = s3Client.getObject(request)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file from S3. key=" + key, e);
        }
    }

    public String getFileUrl(String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
    private String normalizeKey(String key) {
        String prefix = "https://" + bucketName + ".s3.amazonaws.com/";
        if (key != null && key.startsWith(prefix)) {
            return key.substring(prefix.length());
        }
        return key;
    }
}

