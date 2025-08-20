// com.team.startupmatching.storage.StorageResult
package com.team.startupmatching.storage;

/**
 * 스토리지 결과
 * - key : 스토리지 키(상대 경로). 선행 '/' 없음. 예) "shared-office/1/abc.jpg"
 * - url : 공개용 상대 URL. 항상 "/uploads/" + key 로 강제.
 */
public record StorageResult(String key, String url) {
    public StorageResult {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("StorageResult.key must not be blank");
        }
        // key 정규화: 선행 '/' 제거
        key = key.startsWith("/") ? key.substring(1) : key;
        // url은 항상 상대 URL로 강제
        url = "/uploads/" + key;
    }

    /** key만으로 생성 (url은 자동 계산) */
    public static StorageResult ofKey(String key) {
        return new StorageResult(key, null);
    }
}
