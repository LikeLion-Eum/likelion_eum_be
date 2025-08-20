package com.team.startupmatching.service;

import com.team.startupmatching.dto.SharedOfficeCreateRequest;
import com.team.startupmatching.dto.SharedOfficeDetailResponse;
import com.team.startupmatching.dto.SharedOfficeRecommendResponse;
import com.team.startupmatching.dto.SharedOfficeResponse;
import com.team.startupmatching.dto.photo.PhotoItemResponse;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.entity.SharedOfficePhoto;
import com.team.startupmatching.repository.SharedOfficePhotoRepository;
import com.team.startupmatching.repository.SharedOfficeRepository;
import com.team.startupmatching.support.PublicUrlBuilder;
import lombok.RequiredArgsConstructor;
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
    private final PublicUrlBuilder publicUrlBuilder; // 🔹 추가: 절대 URL 생성기

    // 등록
    @Transactional
    public SharedOfficeResponse create(SharedOfficeCreateRequest request) {
        String bizNumber = digitsOnly(request.getBusinessRegistrationNumber());
        String phone     = digitsOnly(request.getHostContact());

        SharedOffice sharedOffice = SharedOffice.builder()
                .name(request.getName())
                .description(request.getDescription())
                .roomCount(request.getRoomCount())
                .size(request.getSize())
                .location(request.getLocation())
                .maxCount(request.getMaxCount())
                .hostBusinessName(request.getHostBusinessName())
                .hostRepresentativeName(request.getHostRepresentativeName())
                .hostAddress(request.getHostAddress())
                .businessRegistrationNumber(bizNumber)
                .hostContact(phone)
                .build();

        SharedOffice saved = sharedOfficeRepository.save(sharedOffice);

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

        // 사진 조회 (seq ASC)
        List<SharedOfficePhoto> photos = photoRepo.findBySharedOfficeIdOrderBySeqAsc(id);

        // 대표 사진: isMain 우선, 없으면 첫 번째
        SharedOfficePhoto main = photos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsMain()))
                .findFirst()
                .orElse(photos.isEmpty() ? null : photos.get(0));

        // 🔹 절대 URL로 변환
        String mainPhotoUrl = main == null ? null : publicUrlBuilder.build(main.getStorageKey());

        List<PhotoItemResponse> photoDtos = photos.stream()
                .map(p -> PhotoItemResponse.builder()
                        .photoId(p.getId())
                        .url(publicUrlBuilder.build(p.getStorageKey())) // 🔹 절대 URL
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
                .facilities(Collections.emptyList())
                .mainPhotoUrl(mainPhotoUrl)      // 🔹 절대 URL
                .photos(photoDtos)               // 🔹 절대 URL
                .hostBusinessName(so.getHostBusinessName())
                .hostRepresentativeName(so.getHostRepresentativeName())
                .hostAddress(so.getHostAddress())
                .businessRegistrationNumber(formattedBiz)
                .hostContact(formattedTel)
                .build();
    }

    // 목록 조회 (기존 그대로)
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
