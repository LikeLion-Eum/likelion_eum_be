// com.team.startupmatching.support.PublicUrlBuilder
package com.team.startupmatching.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * storageKey("shared-office/{id}/{file}.jpg") → 절대 URL 생성기
 * 우선순위:
 * 1) storage.local.public-base-url 사용 (예: https://xxxx.ngrok-free.app/uploads)
 * 2) 미설정 시, 현재 요청 호스트 기준으로 조립 (X-Forwarded-* 반영됨)
 */
@Component
public class PublicUrlBuilder {

    @Value("${storage.local.public-base-url:}")
    private String publicBaseUrl; // 예: https://xxxx.ngrok-free.app/uploads  (또는 /uploads 없이 도메인만)

    public String build(String storageKey) {
        if (!StringUtils.hasText(storageKey)) return null;

        String key = storageKey.startsWith("/") ? storageKey.substring(1) : storageKey;

        // 1) yml 우선
        if (StringUtils.hasText(publicBaseUrl)) {
            String base = publicBaseUrl.trim();
            // 뒤 슬래시 정리
            while (base.endsWith("/")) base = base.substring(0, base.length()-1);
            // base가 /uploads 로 끝나지 않으면 붙여준다
            if (!base.endsWith("/uploads")) base = base + "/uploads";
            return base + "/" + key; // 예: https://.../uploads/shared-office/1/xxx.jpg
        }

        // 2) yml 없으면, 현재 요청 호스트로 절대 URL 생성
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(key)
                .toUriString();
    }
}
