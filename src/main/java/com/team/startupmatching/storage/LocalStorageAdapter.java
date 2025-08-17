package com.team.startupmatching.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component // StoragePort 구현체로 빈 등록
public class LocalStorageAdapter implements StoragePort {

    @Value("${storage.local.base-dir:uploads}")
    private String baseDir; // 예: uploads

    @Value("${storage.local.public-base-url:http://localhost:8080/uploads}")
    private String publicBaseUrl; // 예: http://localhost:8080/uploads

    @Override
    public StorageResult upload(String officeId, MultipartFile file) {
        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext != null ? "." + ext.toLowerCase() : "");
            String relPath = "shared-office/" + officeId + "/" + filename;

            Path base = Paths.get(baseDir).toAbsolutePath().normalize();
            Path target = base.resolve(relPath).normalize();

            Files.createDirectories(target.getParent());
            file.transferTo(target.toFile());

            String url = (publicBaseUrl.endsWith("/") ? publicBaseUrl + relPath : publicBaseUrl + "/" + relPath);
            return new StorageResult(relPath, url);
        } catch (IOException e) {
            throw new RuntimeException("Local upload failed", e);
        }
    }

    @Override
    public List<StorageResult> uploadAll(String officeId, List<MultipartFile> files) {
        return files.stream().map(f -> upload(officeId, f)).collect(Collectors.toList());
    }

    @Override
    public void delete(String key) {
        try {
            Path base = Paths.get(baseDir).toAbsolutePath().normalize();
            Path target = base.resolve(key).normalize();
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new RuntimeException("Local delete failed", e);
        }
    }
}
