package com.team.startupmatching.service;

import com.team.startupmatching.ai.client.AiClient;
import com.team.startupmatching.ai.dto.AiUserSnapshot;
import com.team.startupmatching.dto.UserUpsertRequest;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final AiClient aiClient;

    @Transactional
    public Long upsert(UserUpsertRequest dto) {
        // 1) 신규/수정 분기
        User user = (dto.getId() != null)
                ? userRepository.findById(dto.getId()).orElseGet(User::new)
                : new User();

        // 2) 필드 매핑 (엔티티에 setter 있다고 가정)
        user.setName(dto.getName());
        user.setCareer(dto.getCareer());
        user.setIntroduction(dto.getIntroduction());
        user.setSkills(dto.getSkills());         // "React, Spring" 형태 문자열
        user.setLocation(dto.getLocation());
        user.setResumeUrl(dto.getResumeUrl());

        // 3) 저장
        User saved = userRepository.save(user);

        // 4) AI로 보낼 스냅샷 생성 (PII 제외 규칙은 네가 정한 필드 기준)
        AiUserSnapshot snap = new AiUserSnapshot(
                saved.getId(),
                dto.getName(),
                dto.getCareer(),
                dto.getIntroduction(),
                toList(dto.getSkills()),
                dto.getLocation(),
                dto.getResumeUrl()
        );

        // 5) 모의 AI로 업서트 (예외는 로그만)
        try {
            aiClient.upsertUsers(List.of(snap));
            log.debug("[AI] upsert sent for userId={}", saved.getId());
        } catch (Exception e) {
            log.warn("[AI] upsert failed for userId={} err={}", saved.getId(), e.toString());
        }

        return saved.getId();
    }

    private List<String> toList(String skills) {
        if (skills == null || skills.isBlank()) return List.of();
        return Arrays.stream(skills.split("[,/]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}