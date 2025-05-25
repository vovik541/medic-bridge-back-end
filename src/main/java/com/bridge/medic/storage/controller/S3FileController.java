package com.bridge.medic.storage.controller;

import com.bridge.medic.storage.service.S3StorageService;
import io.awspring.cloud.s3.S3Template;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class S3FileController {

    private final S3StorageService s3StorageService;
    private final S3Template s3Template;
    private final String bucket = "bridge-medic-bucket";

    @GetMapping("/**")
    public ResponseEntity<InputStreamResource> downloadFile(HttpServletRequest request) {
        try {
            String basePath = "/api/v1/files/";
            String fullPath = request.getRequestURI();
            String relativePath = fullPath.substring(fullPath.indexOf(basePath) + basePath.length());

            String decodedKey = URLDecoder.decode(relativePath, StandardCharsets.UTF_8);

            InputStream inputStream = s3Template.download(bucket, decodedKey).getInputStream();

            String contentType = guessContentType(decodedKey);
            String encodedFilename = URLEncoder.encode(decodedKey.substring(decodedKey.lastIndexOf("/") + 1), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String guessContentType(String filename) {
        if (filename.endsWith(".pdf")) return "application/pdf";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".txt")) return "text/plain";
        return "application/octet-stream";
    }

    @DeleteMapping("/{userId}/{filename}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable String filename) {
        s3StorageService.delete(userId, filename);
        return ResponseEntity.noContent().build();
    }
}
