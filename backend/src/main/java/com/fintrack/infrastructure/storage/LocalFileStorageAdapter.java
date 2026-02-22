package com.fintrack.infrastructure.storage;

import com.fintrack.domain.port.out.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path baseDir;

    public LocalFileStorageAdapter(@Value("${app.storage.upload-dir:uploads}") String uploadDir) {
        this.baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String subDirectory) {
        try {
            Path targetDir = baseDir.resolve(subDirectory);
            Files.createDirectories(targetDir);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID() + extension;
            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath);

            return "/uploads/" + subDirectory + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            String relativePath = filePath.startsWith("/uploads/") ? filePath.substring(9) : filePath;
            Path target = baseDir.resolve(relativePath);
            Files.deleteIfExists(target);
        } catch (IOException e) {
            // Log but don't fail
        }
    }
}
