package com.polygon.onlinejudge.services;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadAndGetAddress(MultipartFile file);
}
