package com.polygon.onlinejudge.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class TestlibProvider {

    private final byte[] zippedTestlib;

    public TestlibProvider(@Value("classpath:static/testlib.h") Resource testlibResource) throws IOException {
        byte[] testlibBytes = testlibResource.getInputStream().readAllBytes();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("testlib.h");
            zos.putNextEntry(entry);
            zos.write(testlibBytes);
            zos.closeEntry();
        }
        this.zippedTestlib = baos.toByteArray();
    }

    public String getBase64Zip() {
        return Base64.getEncoder().encodeToString(zippedTestlib);
    }
}