package com.polygon.onlinejudge.services;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String putText(String key, String text);
    String getText(String key);
    void delete(String key);
}
