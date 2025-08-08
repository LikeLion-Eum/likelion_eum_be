package com.team.startupmatching.service;

import com.team.startupmatching.Specification.RecruitmentSpecification;
import com.team.startupmatching.dto.common.SpaceType;              // ✅ enum 경로 여기로 통일!
import com.team.startupmatching.dto.RecruitmentRequest;
import com.team.startupmatching.dto.RecruitmentResponse;
import com.team.startupmatching.entity.Recruitment;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.exception.RecruitmentException;
import com.team.startupmatching.repository.RecruitmentRepository;
import com.team.startupmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.team.startupmatching.Specification.RecruitmentSpecification.containsAllKeywords;
import static com.team.startupmatching.Specification.RecruitmentSpecification.containsKeywordEverywhere;
import static com.team.startupmatching.Specification.RecruitmentSpecification.hasTargetSpaceType;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;

    /** 모집글 등록 */
    @Transactional
    public RecruitmentResponse createRecruitment(RecruitmentRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RecruitmentException("모집글 제목은 필수입니다.", "RECRUITMENT_TITLE_MISSING");
        }
        if (request.getUserId() == null) {
            throw new RecruitmentException("작성자(userId)는 필수입니다.", "RECRUITMENT_USER_MISSING");
        }
        if (request.getTargetSpaceType() == null) {
            throw new RecruitmentException("대상 공간 종류(targetSpaceType)는 필수입니다.", "RECRUITMENT_TARGET_SPACE_TYPE_MISSING");
        }

        User writer = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RecruitmentException("존재하지 않는 사용자입니다.", "USER_NOT_FOUND"));

        Recruitment recruitment = Recruitment.builder()
                .title(request.getTitle())
                .location(request.getLocation())
                .position(request.getPosition())
                .skills(request.getSkills())
                .career(request.getCareer())
                .recruitCount(request.getRecruitCount())
                .content(request.getContent())
                .isClosed(request.getIsClosed() != null ? request.getIsClosed() : Boolean.FALSE)
                .createdAt(LocalDateTime.now())
                .user(writer)
                .targetSpaceType(request.getTargetSpaceType())   // ✅ enum 저장
                .build();

        Recruitment saved = recruitmentRepository.save(recruitment);

        return RecruitmentResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .location(saved.getLocation())
                .position(saved.getPosition())
                .skills(saved.getSkills())
                .career(saved.getCareer())
                .recruitCount(saved.getRecruitCount())
                .content(saved.getContent())
                .isClosed(saved.getIsClosed())
                .createdAt(saved.getCreatedAt())
                .userId(saved.getUser().getId())
                .targetSpaceType(saved.getTargetSpaceType())     // ✅ 응답 포함
                .build();
    }

    /** ✅ 전체 목록 (정렬: 최신순) */
    @Transactional(readOnly = true)
    public List<RecruitmentResponse> listAll() {
        return recruitmentRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt", "id"))
                .stream()
                .map(r -> RecruitmentResponse.builder()
                        .id(r.getId())
                        .title(r.getTitle())
                        .location(r.getLocation())
                        .position(r.getPosition())
                        .skills(r.getSkills())
                        .career(r.getCareer())
                        .recruitCount(r.getRecruitCount())
                        .content(r.getContent())
                        .isClosed(r.getIsClosed())
                        .createdAt(r.getCreatedAt())
                        .userId(r.getUser() != null ? r.getUser().getId() : null)
                        .targetSpaceType(r.getTargetSpaceType())
                        .build())
                .toList();
    }

    /** ✅ JSON 검색(문자열 키워드 AND + enum), 배열 버전으로 확장된 메인 */
    @Transactional(readOnly = true)
    public List<RecruitmentResponse> search(String keyword, SpaceType targetSpaceType, List<String> keywords) {
        Specification<Recruitment> spec = (root, query, cb) -> cb.conjunction();

        // 1) 키워드 배열이 우선 (각 항목 AND)
        if (keywords != null && !keywords.isEmpty()) {
            for (String kw : keywords) {
                if (kw != null && !kw.isBlank()) {
                    spec = spec.and(containsKeywordEverywhere(kw.trim()));
                }
            }
            // 2) 없으면 문자열 하나로 처리 (공백 split AND)
        } else if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(containsAllKeywords(keyword));
        }

        // 3) enum 필터(옵션)
        if (targetSpaceType != null) {
            spec = spec.and(hasTargetSpaceType(targetSpaceType));
        }

        return recruitmentRepository
                .findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt", "id"))
                .stream()
                .map(r -> RecruitmentResponse.builder()
                        .id(r.getId())
                        .title(r.getTitle())
                        .location(r.getLocation())
                        .position(r.getPosition())
                        .skills(r.getSkills())
                        .career(r.getCareer())
                        .recruitCount(r.getRecruitCount())
                        .content(r.getContent())
                        .isClosed(r.getIsClosed())
                        .createdAt(r.getCreatedAt())
                        .userId(r.getUser() != null ? r.getUser().getId() : null)
                        .targetSpaceType(r.getTargetSpaceType())
                        .build())
                .toList();
    }

    /** ✅ (호환용) 예전 2파라미터 시그니처 → 새 메서드로 위임 */
    @Transactional(readOnly = true)
    public List<RecruitmentResponse> search(String keyword, SpaceType targetSpaceType) {
        return search(keyword, targetSpaceType, null);
    }
}
