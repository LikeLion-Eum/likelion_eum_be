package com.team.startupmatching.service;


import com.team.startupmatching.dto.SharedOfficeCreateRequest;
import com.team.startupmatching.dto.SharedOfficePatchRequest;
import com.team.startupmatching.dto.SharedOfficeResponse;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.event.SharedOfficeChangedEvent;
import com.team.startupmatching.repository.SharedOfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharedOfficeService {

    private final SharedOfficeRepository sharedOfficeRepository;
    private final ApplicationEventPublisher publisher;


    // 등록 /** 생성? **/
    @Transactional
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

        publisher.publishEvent(new SharedOfficeChangedEvent(saved.getId()));

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

    /** 부분 수정 (null이 아닌 필드만 반영) **/
    @Transactional
    public SharedOfficeResponse patch(long id, SharedOfficePatchRequest req) {
        SharedOffice so = sharedOfficeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SharedOffice not found: " + id));

        if (req.getName() != null) so.setName(req.getName());
        if (req.getLocation() != null) so.setLocation(req.getLocation());
        if (req.getDescription() != null) so.setDescription(req.getDescription());
        if (req.getRoomCount() != null) so.setRoomCount(req.getRoomCount());
        if (req.getSize() != null) so.setSize(req.getSize());
        if (req.getMaxCount() != null) so.setMaxCount(req.getMaxCount());

        SharedOffice saved = sharedOfficeRepository.save(so);

        publisher.publishEvent(new SharedOfficeChangedEvent(saved.getId())); // 이벤트 발행 AFTER_COMMIT

        // 응답 DTO 생성
        return SharedOfficeResponse.from(saved);
    }
}
