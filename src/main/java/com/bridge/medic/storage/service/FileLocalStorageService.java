package com.bridge.medic.storage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileLocalStorageService implements StorageServiceI {

    private final Path uploadDir;

    public FileLocalStorageService(@Value("${file.upload-dir:uploads}") String uploadDirName) {
        this.uploadDir = Paths.get(uploadDirName).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload folder", ex);
        }
    }

    public String storeFile(MultipartFile file, String subPath) {
        String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

        Path targetDirectory = uploadDir.resolve(subPath);
        try {
            Files.createDirectories(targetDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося створити підкаталог: " + targetDirectory, e);
        }

        Path target = targetDirectory.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/files/" + subPath + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Не вдалося зберегти файл: " + target, ex);
        }
    }


    public Path load(String filename) {
        return uploadDir.resolve(filename).normalize();
    }

}