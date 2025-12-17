package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    // Kabul edilen dosya tipleri
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg"
    );

    // Proje root'una göre dosyaların kaydedileceği klasör
    private final Path rootLocation = Paths.get("uploads/clinic-documents");

    @Override
    public String storeClinicDocument(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Document file is required");
        }

        if (file.getContentType() == null ||
                !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF, PNG or JPG files are allowed");
        }

        try {
            // Klasör yoksa oluştur
            Files.createDirectories(rootLocation);

            // Güvenli dosya adı üret
            String extension = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString()
                    + (extension.isBlank() ? "" : "." + extension);

            Path destination = rootLocation.resolve(filename).normalize();

            // Dosyayı kaydet
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // DB'ye yazılacak path
            return destination.toString().replace("\\", "/");

        } catch (IOException e) {
            throw new RuntimeException("Failed to store clinic document", e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int index = filename.lastIndexOf('.');
        if (index == -1 || index == filename.length() - 1) return "";
        return filename.substring(index + 1).toLowerCase();
    }
}
