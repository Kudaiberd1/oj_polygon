package com.polygon.onlinejudge.controllers;

import com.polygon.onlinejudge.services.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/polygon/files")
public class FileProxyController {

    private final S3Service s3Service;

    public FileProxyController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/**")
    public ResponseEntity<byte[]> proxyFile(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String prefix = "/api/v1/polygon/files/";

        if (!requestUri.startsWith(prefix)) {
            return ResponseEntity.badRequest().build();
        }

        String key = URLDecoder.decode(
                requestUri.substring(prefix.length()),
                StandardCharsets.UTF_8
        );

        if (key.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] content = s3Service.getBytes(key);

            MediaType mediaType = MediaType.TEXT_PLAIN;
            if (key.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
            if (key.endsWith(".jpg") || key.endsWith(".jpeg")) mediaType = MediaType.IMAGE_JPEG;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(content);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
