package com.team.startupmatching.service;

import com.team.startupmatching.dto.UserUpsertRequest;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.event.UserChangedEvent;
import com.team.startupmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Long upsert(UserUpsertRequest dto) {
        // 1) 신규/수정 분기
        User user = (dto.getId() != null)
                ? userRepository.findById(dto.getId()).orElseGet(User::new)
                : new User();

        // 2) 필드 매핑
        user.setName(dto.getName());
        user.setCareer(dto.getCareer());
        user.setIntroduction(dto.getIntroduction());
        user.setSkills(dto.getSkills());   // "React, Spring" 형태 문자열
        user.setLocation(dto.getLocation());
        user.setResumeUrl(dto.getResumeUrl());

        // 3) 저장
        User saved = userRepository.save(user);

        // 4) 저장 성공 후, 커밋되면 전송되도록 이벤트 발행
        publisher.publishEvent(new UserChangedEvent(saved.getId()));
        log.debug("[EVENT] UserChangedEvent published userId={}", saved.getId());

        // (중요) 여기서는 aiClient를 직접 호출하지 않음. (중복 전송 방지)
        return saved.getId();
    }

    // 필요시 CSV → List 변환 보조 (현재는 사용 X)
    private List<String> toList(String skills) {
        if (skills == null || skills.isBlank()) return List.of();
        return Arrays.stream(skills.split("[,/]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
