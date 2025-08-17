package com.team.startupmatching.service;


import com.team.startupmatching.dto.SharedOfficeCreateRequest;
import com.team.startupmatching.dto.SharedOfficeRecommendResponse;
import com.team.startupmatching.dto.SharedOfficeResponse;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.repository.SharedOfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharedOfficeService {

    private final SharedOfficeRepository sharedOfficeRepository;

    // 등록
    public SharedOfficeResponse create(SharedOfficeCreateRequest request) {
        SharedOffice sharedOffice = SharedOffice.builder()
                .name(request.getName())
                .description(request.getDescription())
                .roomCount(request.getRoomCount())
                .size(request.getSize())
                .location(request.getLocation())
                .maxCount(request.getMaxCount())
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

    // 목록 조회
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

    @Transactional(readOnly = true)
    public List<SharedOfficeRecommendResponse> recommendByLocation(String location) {
        List<SharedOffice> foundOffices = sharedOfficeRepository.findByLocationContainingIgnoreCase(location);

        return foundOffices.stream()
                .map(SharedOfficeRecommendResponse::from)
                .collect(Collectors.toList());
    }
}
