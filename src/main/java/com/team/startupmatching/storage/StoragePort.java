// com.team.startupmatching.storage.StoragePort
package com.team.startupmatching.storage;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StoragePort {
    StorageResult upload(String officeId, MultipartFile file);
    List<StorageResult> uploadAll(String officeId, List<MultipartFile> files);
    void delete(String key);
}
