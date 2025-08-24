package com.team.startupmatching.service;

import com.team.startupmatching.dto.RecruitmentRequest;
import com.team.startupmatching.dto.RecruitmentResponse;
import com.team.startupmatching.dto.RecruitmentUpdateRequest;
import com.team.startupmatching.entity.Recruitment;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.exception.RecruitmentException;
import com.team.startupmatching.repository.RecruitmentRepository;
import com.team.startupmatching.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.team.startupmatching.Specification.RecruitmentSpecification.containsAllKeywords;
import static com.team.startupmatching.Specification.RecruitmentSpecification.containsKeywordEverywhere;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;

    /**
     * 모집글 등록
     */
    @Transactional
    public RecruitmentResponse createRecruitment(RecruitmentRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RecruitmentException("모집글 제목은 필수입니다.", "RECRUITMENT_TITLE_MISSING");
        }
        if (request.getUserId() == null) {
            throw new RecruitmentException("작성자(userId)는 필수입니다.", "RECRUITMENT_USER_MISSING");
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
                .build();

        Recruitment saved = recruitmentRepository.save(recruitment);
        return RecruitmentResponse.from(saved);
    }

    /**
     * 전체 목록 (정렬: 최신순)
     */
    @Transactional(readOnly = true)
    public List<RecruitmentResponse> listAll() {
        return recruitmentRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt", "id"))
                .stream()
                .map(RecruitmentResponse::from)
                .toList();
    }

    /**
     * 검색: 키워드 배열(AND) 또는 단일 문자열(공백 split AND)
     */
    @Transactional(readOnly = true)
    public List<RecruitmentResponse> search(String keyword, List<String> keywords) {
        Specification<Recruitment> spec = (root, query, cb) -> cb.conjunction();

        if (keywords != null && !keywords.isEmpty()) {
            for (String kw : keywords) {
                if (kw != null && !kw.isBlank()) {
                    spec = spec.and(containsKeywordEverywhere(kw.trim()));
                }
            }
        } else if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(containsAllKeywords(keyword));
        }

        return recruitmentRepository
                .findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt", "id"))
                .stream()
                .map(RecruitmentResponse::from)
                .toList();
    }

    /** (호환용) */
    @Transactional(readOnly = true)
    public List<RecruitmentResponse> search(String keyword) {
        return search(keyword, null);
    }

    @Transactional(readOnly = true)
    public RecruitmentResponse getOne(Long id) {
        Recruitment r = recruitmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recruitment not found: " + id));
        return RecruitmentResponse.from(r);
    }

    /* =========================
     *       UPDATE / DELETE
     * ========================= */

    /**
     * 전체 치환(모든 필드 필수) — PUT
     * - 제목/작성자 등 필수값 검증
     * - createdAt은 유지
     */
    @Transactional
    public RecruitmentResponse updateReplace(Long id, RecruitmentRequest req) {
        Recruitment e = recruitmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recruitment not found: " + id));

        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new RecruitmentException("모집글 제목은 필수입니다.", "RECRUITMENT_TITLE_MISSING");
        }
        if (req.getUserId() == null) {
            throw new RecruitmentException("작성자(userId)는 필수입니다.", "RECRUITMENT_USER_MISSING");
        }

        User writer = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RecruitmentException("존재하지 않는 사용자입니다.", "USER_NOT_FOUND"));

        // 전체 치환 (createdAt은 유지)
        e.setTitle(req.getTitle());
        e.setLocation(req.getLocation());
        e.setPosition(req.getPosition());
        e.setSkills(req.getSkills());
        e.setCareer(req.getCareer());
        e.setRecruitCount(req.getRecruitCount());
        e.setContent(req.getContent());
        e.setIsClosed(req.getIsClosed() != null ? req.getIsClosed() : Boolean.FALSE);
        e.setUser(writer);

        // Dirty checking으로 반영
        return RecruitmentResponse.from(e);
    }

    /**
     * 부분 수정 — PATCH
     * - null이 아닌 필드만 반영
     */
    @Transactional
    public RecruitmentResponse updatePartial(Long id, RecruitmentUpdateRequest req) {
        Recruitment e = recruitmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recruitment not found: " + id));

        if (req.getTitle() != null) e.setTitle(req.getTitle());
        if (req.getLocation() != null) e.setLocation(req.getLocation());
        if (req.getPosition() != null) e.setPosition(req.getPosition());
        if (req.getSkills() != null) e.setSkills(req.getSkills());
        if (req.getCareer() != null) e.setCareer(req.getCareer());
        if (req.getRecruitCount() != null) e.setRecruitCount(req.getRecruitCount());
        if (req.getContent() != null) e.setContent(req.getContent());
        if (req.getIsClosed() != null) e.setIsClosed(req.getIsClosed());

        if (req.getUserId() != null) {
            User writer = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new RecruitmentException("존재하지 않는 사용자입니다.", "USER_NOT_FOUND"));
            e.setUser(writer);
        }

        // Dirty checking으로 반영
        return RecruitmentResponse.from(e);
    }

    /**
     * 삭제 — DELETE
     */
    @Transactional
    public void delete(Long id) {
        Recruitment e = recruitmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recruitment not found: " + id));
        recruitmentRepository.delete(e);
    }
}
