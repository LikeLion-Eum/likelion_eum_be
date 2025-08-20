// com.team.startupmatching.storage.StoragePort
package com.team.startupmatching.storage;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 스토리지 어댑터.
 *
 * 규칙:
 * - StorageResult.key : 스토리지 키(상대 경로). 선행 '/' 없음, "/uploads" 프리픽스 없음.
 *                       예) "shared-office/{officeId}/{uuid}.jpg"
 * - StorageResult.url : 공개용 "상대 URL". 항상 "/uploads/" + key 형태.
 *                       예) "/uploads/shared-office/{officeId}/{uuid}.jpg"
 *
 * DB에는 절대 URL을 저장하지 말고, 오직 key만 저장하세요.
 */
public interface StoragePort {
    StorageResult upload(String officeId, MultipartFile file);
    List<StorageResult> uploadAll(String officeId, List<MultipartFile> files);
    void delete(String key); // 매개값은 StorageResult.key 와 동일 포맷
}
