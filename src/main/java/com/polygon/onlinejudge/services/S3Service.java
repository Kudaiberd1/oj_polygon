package com.polygon.onlinejudge.services;


public interface S3Service {
    String putText(String key, String text);
    String getText(String key);
    String getInput(String key);
    void delete(String key);
}
