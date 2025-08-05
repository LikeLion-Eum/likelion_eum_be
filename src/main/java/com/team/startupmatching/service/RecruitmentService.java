package com.team.startupmatching.service;

import com.team.startupmatching.dto.RecruitmentRequest;
import com.team.startupmatching.dto.RecruitmentResponse;
import com.team.startupmatching.entity.Recruitment;
import com.team.startupmatching.exception.RecruitmentException;
import com.team.startupmatching.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 모집글 등록 서비스
 */
@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    /**
     * 모집글 등록
     */
    public RecruitmentResponse createRecruitment(RecruitmentRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RecruitmentException("모집글 제목은 필수입니다.", "RECRUITMENT_TITLE_MISSING");
        }

        if (request.getWriter() == null || request.getWriter().isBlank()) {
            throw new RecruitmentException("작성자 이름은 필수입니다.", "RECRUITMENT_WRITER_MISSING");
        }

        // 1. 요청 DTO → 엔티티로 변환
        Recruitment recruitment = Recruitment.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(request.getWriter())
                .contact(request.getContact())
                .spaceName(request.getSpaceName())
                .spaceLocation(request.getSpaceLocation())
                .createdAt(LocalDateTime.now()) // 등록 시각 설정
                .build();

        // 2. 저장
        Recruitment saved = recruitmentRepository.save(recruitment);

        // 3. 저장된 엔티티 → 응답 DTO로 변환
        return RecruitmentResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .writer(saved.getWriter())
                .contact(saved.getContact())
                .spaceName(saved.getSpaceName())
                .spaceLocation(saved.getSpaceLocation())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}