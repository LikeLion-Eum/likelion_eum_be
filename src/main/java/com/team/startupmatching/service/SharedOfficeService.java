package com.team.startupmatching.service;

import com.team.startupmatching.Specification.SharedOfficeSpecification;
import com.team.startupmatching.dto.*;
import com.team.startupmatching.dto.photo.PhotoItemResponse;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.entity.SharedOfficePhoto;
import com.team.startupmatching.repository.SharedOfficePhotoRepository;
import com.team.startupmatching.repository.SharedOfficeRepository;
import com.team.startupmatching.support.PublicUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SharedOfficeService {

    private final SharedOfficeRepository sharedOfficeRepository;
    private final SharedOfficePhotoRepository photoRepo;
    private final PublicUrlBuilder publicUrlBuilder;

    /* -------------------------------------------
     * 목록 검색: keyword + 요금 범위 + 페이지네이션(1-base)
     * ------------------------------------------- */
    @Transactional(readOnly = true)
    public Page<SharedOfficeResponse> search(
            String keyword,
            Long minFeeMonthly,
            Long maxFeeMonthly,
            int page,      // 1-base
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "id"));

        Specification<SharedOffice> spec = (root, q, cb) -> cb.conjunction();
        Specification<SharedOffice> k  = SharedOfficeSpecification.keywordLike(keyword);
        Specification<SharedOffice> ge = SharedOfficeSpecification.feeMonthlyGoe(minFeeMonthly);
        Specification<SharedOffice> le = SharedOfficeSpecification.feeMonthlyLoe(maxFeeMonthly);
        if (k  != null) spec = spec.and(k);
        if (ge != null) spec = spec.and(ge);
        if (le != null) spec = spec.and(le);

        return sharedOfficeRepository.findAll(spec, pageable).map(this::toResponse);
    }

    // 단순 전체 목록 (필요 시 유지)
    @Transactional(readOnly = true)
    public List<SharedOfficeResponse> list() {
        return sharedOfficeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 등록
    @Transactional
    public SharedOfficeResponse create(SharedOfficeCreateRequest request) {
        String bizNumber = digitsOnly(request.getBusinessRegistrationNumber());
        String phone     = digitsOnly(request.getHostContact());

        SharedOffice sharedOffice = SharedOffice.builder()
                // 기본 정보
                .name(request.getName())
                .description(request.getDescription())
                .roomCount(request.getRoomCount())
                .size(request.getSize())
                .location(request.getLocation())
                .maxCount(request.getMaxCount())
                .feeMonthly(request.getFeeMonthly())
                // 호스트 정보
                .hostRepresentativeName(request.getHostRepresentativeName())
                .businessRegistrationNumber(bizNumber)
                .hostContact(phone)
                .build();

        SharedOffice saved = sharedOfficeRepository.save(sharedOffice);
        return toResponse(saved);
    }

    // 단건 조회 (상세)
    @Transactional(readOnly = true)
    public SharedOfficeDetailResponse getOne(Long id) {
        SharedOffice so = sharedOfficeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "SharedOffice not found: " + id));

        // 사진 조회 (seq ASC)
        List<SharedOfficePhoto> photos = photoRepo.findBySharedOfficeIdOrderBySeqAsc(id);

        SharedOfficePhoto main = photos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsMain()))
                .findFirst()
                .orElse(photos.isEmpty() ? null : photos.get(0));

        String mainPhotoUrl = main == null ? null : publicUrlBuilder.build(main.getStorageKey());

        List<PhotoItemResponse> photoDtos = photos.stream()
                .map(p -> PhotoItemResponse.builder()
                        .photoId(p.getId())
                        .url(publicUrlBuilder.build(p.getStorageKey()))
                        .seq(p.getSeq())
                        .isMain(p.getIsMain())
                        .caption(p.getCaption())
                        .build())
                .collect(Collectors.toList());

        String formattedBiz = formatBizNo(so.getBusinessRegistrationNumber());
        String formattedTel = formatPhone(so.getHostContact());

        return SharedOfficeDetailResponse.builder()
                .id(so.getId())
                .name(so.getName())
                .description(so.getDescription())
                .roomCount(so.getRoomCount())
                .size(so.getSize())
                .location(so.getLocation())
                .maxCount(so.getMaxCount())
                .feeMonthly(so.getFeeMonthly())
                .facilities(Collections.emptyList())
                .mainPhotoUrl(mainPhotoUrl)
                .photos(photoDtos)
                // 호스트 정보
                .hostRepresentativeName(so.getHostRepresentativeName())
                .businessRegistrationNumber(formattedBiz)
                .hostContact(formattedTel)
                .build();
    }

    // 지역 기반 추천
    @Transactional(readOnly = true)
    public List<SharedOfficeRecommendResponse> recommendByLocation(String location) {
        List<SharedOffice> foundOffices =
                sharedOfficeRepository.findByLocationContainingIgnoreCase(location);
        return foundOffices.stream()
                .map(SharedOfficeRecommendResponse::from)
                .collect(Collectors.toList());
    }

    /* =========================
     *      수정 & 삭제 추가
     * ========================= */

    /** 전체 수정(치환) — 모든 필드 필수(컨트롤러에서 검증) */
    @Transactional
    public SharedOfficeResponse update(Long id, SharedOfficeUpdateRequest req) {
        SharedOffice e = sharedOfficeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "SharedOffice not found: " + id));

        // 전체 치환 (PUT) – 요청 필드 모두 필수라는 전제
        e.setName(req.getName());
        e.setDescription(req.getDescription());
        e.setRoomCount(req.getRoomCount());
        e.setSize(req.getSize());
        e.setLocation(req.getLocation());
        e.setMaxCount(req.getMaxCount());
        e.setFeeMonthly(req.getFeeMonthly());

        e.setHostRepresentativeName(req.getHostRepresentativeName());
        e.setBusinessRegistrationNumber(digitsOnly(req.getBusinessRegistrationNumber()));
        e.setHostContact(digitsOnly(req.getHostContact()));

        // Dirty checking으로 업데이트 반영
        return toResponse(e); // ← 요약 응답으로 맞춰줍니다.
    }


    /** 부분 수정 — null 이 아닌 값만 반영 */
    @Transactional
    public SharedOfficeResponse patch(Long id, SharedOfficePatchRequest req) {
        SharedOffice e = sharedOfficeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "SharedOffice not found: " + id));

        if (req.getName() != null) e.setName(req.getName());
        if (req.getDescription() != null) e.setDescription(req.getDescription());
        if (req.getRoomCount() != null) e.setRoomCount(req.getRoomCount());
        if (req.getSize() != null) e.setSize(req.getSize());
        if (req.getLocation() != null) e.setLocation(req.getLocation());
        if (req.getMaxCount() != null) e.setMaxCount(req.getMaxCount());
        if (req.getFeeMonthly() != null) e.setFeeMonthly(req.getFeeMonthly());
        if (req.getHostRepresentativeName() != null) e.setHostRepresentativeName(req.getHostRepresentativeName());

        if (req.getBusinessRegistrationNumber() != null) {
            e.setBusinessRegistrationNumber(digitsOnly(req.getBusinessRegistrationNumber()));
        }
        if (req.getHostContact() != null) {
            e.setHostContact(digitsOnly(req.getHostContact()));
        }

        return toResponse(e);
    }

    /** 삭제 */
    @Transactional
    public void delete(Long id) {
        SharedOffice e = sharedOfficeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "SharedOffice not found: " + id));

        // 사진이 외래키로 묶여 있고 cascade 가 없다면 직접 삭제
        List<SharedOfficePhoto> photos = photoRepo.findBySharedOfficeIdOrderBySeqAsc(id);
        if (!photos.isEmpty()) {
            photoRepo.deleteAll(photos);
        }
        sharedOfficeRepository.delete(e);
    }

    /* mapper 공통화 */
    private SharedOfficeResponse toResponse(SharedOffice so) {
        return new SharedOfficeResponse(
                so.getId(),
                so.getName(),
                so.getLocation(),
                so.getSize(),
                so.getMaxCount(),
                so.getFeeMonthly(),
                so.getDescription(),
                so.getHostRepresentativeName(),
                so.getBusinessRegistrationNumber(),
                so.getHostContact()
        );
    }

    /* helpers */
    private String formatBizNo(String digits) {
        if (digits == null) return null;
        String d = digitsOnly(digits);
        if (d.length() == 10) {
            return d.substring(0, 3) + "-" + d.substring(3, 5) + "-" + d.substring(5);
        }
        return digits;
    }

    private String formatPhone(String digits) {
        if (digits == null) return null;
        String d = digitsOnly(digits);
        if (d.startsWith("02")) {
            if (d.length() == 9)  return "02-" + d.substring(2, 5) + "-" + d.substring(5);
            if (d.length() == 10) return "02-" + d.substring(2, 6) + "-" + d.substring(6);
            return digits;
        }
        if (d.length() == 10) return d.substring(0,3)+"-"+d.substring(3,6)+"-"+d.substring(6);
        if (d.length() == 11) return d.substring(0,3)+"-"+d.substring(3,7)+"-"+d.substring(7);
        return digits;
    }

    private String digitsOnly(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }
}
