package com.fintrack.domain.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    /** Stores the file and returns the public URL path (e.g., /uploads/attachments/uuid.jpg). */
    String store(MultipartFile file, String subDirectory);
    void delete(String filePath);
}
