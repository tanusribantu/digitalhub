package com.digitalhub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadLocation;

    public FileStorageService(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation.resolve("products"));
            Files.createDirectories(this.uploadLocation.resolve("previews"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    public String storeProductFile(MultipartFile file) {
        return store(file, "products");
    }

    public String storePreviewImage(MultipartFile file) {
        return store(file, "previews");
    }

    private String store(MultipartFile file, String subDir) {
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex);
        }
        String storedFileName = UUID.randomUUID().toString() + extension;
        try {
            Path targetLocation = this.uploadLocation.resolve(subDir).resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + originalName, ex);
        }
    }

    public Resource loadFileAsResource(String subDir, String fileName) {
        try {
            Path filePath = this.uploadLocation.resolve(subDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File path error: " + fileName, ex);
        }
    }

    public void createMockDigitalAsset(String fileName, String content) {
        try {
            Path targetLocation = this.uploadLocation.resolve("products").resolve(fileName);
            if (!Files.exists(targetLocation)) {
                Files.writeString(targetLocation, content);
            }
        } catch (IOException e) {
            // ignore
        }
    }
}