package com.team.startupmatching.service;

import com.team.startupmatching.dto.SharedOfficeCreateRequest;
import com.team.startupmatching.dto.SharedOfficeDetailResponse;
import com.team.startupmatching.dto.SharedOfficeRecommendResponse;
import com.team.startupmatching.dto.SharedOfficeResponse;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.repository.SharedOfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SharedOfficeService {

    private final SharedOfficeRepository sharedOfficeRepository;

    // 등록
    @Transactional
    public SharedOfficeResponse create(SharedOfficeCreateRequest request) {
        // 사업자번호/연락처는 숫자만 저장 (하이픈, 공백 등 제거)
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
                // 호스트 정보
                .hostBusinessName(request.getHostBusinessName())
                .hostRepresentativeName(request.getHostRepresentativeName())
                .hostAddress(request.getHostAddress())
                .businessRegistrationNumber(bizNumber)
                .hostContact(phone)
                .build();

        SharedOffice saved = sharedOfficeRepository.save(sharedOffice);

        // 목록/간단 응답은 기존 스펙 유지
        return new SharedOfficeResponse(
                saved.getId(),
                saved.getName(),
                saved.getLocation(),
                saved.getSize(),
                saved.getMaxCount()
        );
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public SharedOfficeDetailResponse getOne(Long id) {
        SharedOffice so = sharedOfficeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "SharedOffice not found: " + id));

        String formattedBiz = formatBizNo(so.getBusinessRegistrationNumber()); // 123-45-67890
        String formattedTel = formatPhone(so.getHostContact());                // 010-1234-5678 등

        return SharedOfficeDetailResponse.builder()
                .id(so.getId())
                .name(so.getName())
                .description(so.getDescription())
                .roomCount(so.getRoomCount())
                .size(so.getSize())
                .location(so.getLocation())
                .maxCount(so.getMaxCount())
                // 아직 없으면 빈값/NULL로 유지
                .facilities(java.util.Collections.emptyList())
                .mainPhotoUrl(null)
                .photos(java.util.Collections.emptyList())
                // 호스트 정보(표시용)
                .hostBusinessName(so.getHostBusinessName())
                .hostRepresentativeName(so.getHostRepresentativeName())
                .hostAddress(so.getHostAddress())
                .businessRegistrationNumber(formattedBiz)
                .hostContact(formattedTel)
                .build();
    }

    private String formatBizNo(String digits) {
        if (digits == null) return null;
        String d = digitsOnly(digits);
        if (d.length() == 10) {
            return d.substring(0, 3) + "-" + d.substring(3, 5) + "-" + d.substring(5);
        }
        return digits; // 길이 예외면 원본 반환
    }

    // 전화번호 포맷(간단)
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

    // 목록 조회
    @Transactional(readOnly = true)
    public List<SharedOfficeResponse> list() {
        return sharedOfficeRepository.findAll().stream()
                .map(so -> new SharedOfficeResponse(
                        so.getId(),
                        so.getName(),
                        so.getLocation(),
                        so.getSize(),
                        so.getMaxCount()
                ))
                .collect(Collectors.toList());
    }

    // 지역 기반 추천(간단 검색)
    @Transactional(readOnly = true)
    public List<SharedOfficeRecommendResponse> recommendByLocation(String location) {
        List<SharedOffice> foundOffices =
                sharedOfficeRepository.findByLocationContainingIgnoreCase(location);

        return foundOffices.stream()
                .map(SharedOfficeRecommendResponse::from)
                .collect(Collectors.toList());
    }

    // 숫자만 남기는 헬퍼
    private String digitsOnly(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }
}
