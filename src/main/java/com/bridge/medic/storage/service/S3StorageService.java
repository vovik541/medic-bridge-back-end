package com.bridge.medic.storage.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Template s3Template;
    private final String bucket = "bridge-medic-bucket";

    public void upload(String key, MultipartFile file) {
        try {
            s3Template.upload(bucket, key, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Upload error", e);
        }
    }

    public byte[] download(Long userId, String filename) {
        String key = buildKey(userId, filename);
        try (var is = s3Template.download(bucket, key).getInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Download error", e);
        }
    }

    public void delete(Long userId, String filename) {
        String key = buildKey(userId, filename);
        s3Template.deleteObject(bucket, key);
    }

    private String buildKey(Long userId, String filename) {
        return "user-files/" + userId + "/" + filename;
    }
}
