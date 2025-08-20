// com.team.startupmatching.storage.LocalStorageAdapter
package com.team.startupmatching.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Primary
public class LocalStorageAdapter implements StoragePort {

    /**
     * 디스크 업로드 루트. StaticResourceConfig가
     *   /uploads/**  ->  file:{baseDir}/**
     * 로 매핑합니다.
     */
    @Value("${storage.local.base-dir:uploads}")
    private String baseDir;

    @Override
    public StorageResult upload(String officeId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        String ext = getExtOrNull(original);
        String filename = UUID.randomUUID().toString() + (ext == null ? "" : "." + ext.toLowerCase());

        // ⚠️ key는 'uploads/' 미포함 & 선행 '/' 없음
        String key = "shared-office/" + officeId + "/" + filename;

        Path root = Paths.get(baseDir).toAbsolutePath().normalize();
        Path target = root.resolve(key).normalize();

        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target.toFile());
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패: " + target, e);
        }

        // StorageResult가 url을 항상 "/uploads/" + key 로 강제함
        return StorageResult.ofKey(key);
    }

    @Override
    public List<StorageResult> uploadAll(String officeId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();
        List<StorageResult> out = new ArrayList<>(files.size());
        for (MultipartFile f : files) {
            out.add(upload(officeId, f));
        }
        return out;
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) return;
        String normalizedKey = key.startsWith("/") ? key.substring(1) : key;

        Path root = Paths.get(baseDir).toAbsolutePath().normalize();
        Path target = root.resolve(normalizedKey).normalize();

        try {
            Files.deleteIfExists(target);
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 실패: " + target, e);
        }
    }

    /* helper */
    private static String getExtOrNull(String filename) {
        int i = filename.lastIndexOf('.');
        if (i >= 0 && i < filename.length() - 1) return filename.substring(i + 1);
        return null;
    }
}
