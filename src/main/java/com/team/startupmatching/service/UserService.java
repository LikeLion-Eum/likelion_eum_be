// src/main/java/com/team/startupmatching/service/UserService.java
package com.team.startupmatching.service;

import com.team.startupmatching.dto.UserCreateRequest;
import com.team.startupmatching.dto.UserPatchRequest;
import com.team.startupmatching.dto.UserResponse;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.event.UserChangedEvent;
import com.team.startupmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    /** 생성 */
    @Transactional
    public UserResponse create(UserCreateRequest req) {
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setLocation(req.getLocation());
        u.setIntroduction(req.getIntroduction());
        u.setSkills(req.getSkills());
        u.setCareer(req.getCareer());
        u.setResumeUrl(req.getResumeUrl());

        User saved = userRepository.save(u);               // createdAt/updatedAt 자동 세팅
        publisher.publishEvent(new UserChangedEvent(saved.getId())); // AFTER_COMMIT에서 AI 전송
        return UserResponse.from(saved);
    }

    /** 부분 수정 (null 아닌 필드만 반영) */
    @Transactional
    public UserResponse patch(long id, UserPatchRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (req.getName() != null)         u.setName(req.getName());
        if (req.getLocation() != null)     u.setLocation(req.getLocation());
        if (req.getIntroduction() != null) u.setIntroduction(req.getIntroduction());
        if (req.getSkills() != null)       u.setSkills(req.getSkills());
        if (req.getCareer() != null)       u.setCareer(req.getCareer());
        if (req.getResumeUrl() != null)    u.setResumeUrl(req.getResumeUrl());

        User saved = userRepository.save(u);               // updatedAt 자동 갱신
        publisher.publishEvent(new UserChangedEvent(saved.getId())); // AFTER_COMMIT
        return UserResponse.from(saved);
    }
}
