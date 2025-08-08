package com.team.startupmatching.service;

import com.team.startupmatching.dto.IncubationCenterCreateRequest;
import com.team.startupmatching.dto.IncubationCenterResponse;
import com.team.startupmatching.entity.IncubationCenter;
import com.team.startupmatching.repository.IncubationCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncubationCenterService {

    private final IncubationCenterRepository incubationCenterRepository;

    // 등록
    public IncubationCenterResponse create(IncubationCenterCreateRequest request) {
        IncubationCenter ic = IncubationCenter.builder()
                .description(request.getDescription())
                .region(request.getRegion())
                .siteUrl(request.getSiteUrl())
                .build();

        IncubationCenter saved = incubationCenterRepository.save(ic);

        return new IncubationCenterResponse(
                saved.getId(),
                saved.getDescription(),
                saved.getRegion(),
                saved.getSiteUrl()
        );
    }

    // 목록 조회
    public List<IncubationCenterResponse> list() {
        return incubationCenterRepository.findAll().stream()
                .map(ic -> new IncubationCenterResponse(
                        ic.getId(),
                        ic.getDescription(),
                        ic.getRegion(),
                        ic.getSiteUrl()
                ))
                .collect(Collectors.toList());
    }
}