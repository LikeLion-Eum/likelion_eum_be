// src/main/java/com/team/startupmatching/service/UserService.java
package com.team.startupmatching.service;

import com.team.startupmatching.dto.UserCreateRequest;
import com.team.startupmatching.dto.UserPatchRequest;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.event.UserChangedEvent; // ✅ 네가 만든 패키지
import com.team.startupmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;   // ✅ 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;   // ✅ 추가

    @Transactional
    public Long create(UserCreateRequest req) {
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setLocation(req.getLocation());
        u.setIntroduction(req.getIntroduction());
        u.setSkills(req.getSkills());
        u.setCareer(req.getCareer());
        u.setResumeUrl(req.getResumeUrl());

        Long id = userRepository.save(u).getId();
        publisher.publishEvent(new UserChangedEvent(id)); // ✅ 이벤트 발행
        return id;
    }

    @Transactional
    public Long patch(long id, UserPatchRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (req.getName() != null)         u.setName(req.getName());
        if (req.getLocation() != null)     u.setLocation(req.getLocation());
        if (req.getIntroduction() != null) u.setIntroduction(req.getIntroduction());
        if (req.getSkills() != null)       u.setSkills(req.getSkills());
        if (req.getCareer() != null)       u.setCareer(req.getCareer());
        if (req.getResumeUrl() != null)    u.setResumeUrl(req.getResumeUrl());

        Long savedId = userRepository.save(u).getId();
        publisher.publishEvent(new UserChangedEvent(savedId)); // ✅ 이벤트 발행
        return savedId;
    }
}
