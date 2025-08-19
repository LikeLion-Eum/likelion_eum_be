package com.team.startupmatching.controller;

import com.team.startupmatching.dto.ReservationCreateRequest;
import com.team.startupmatching.dto.ReservationResponse;
import com.team.startupmatching.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 생성
     * POST /api/shared-offices/{officeId}/reservations
     */
    @PostMapping("/shared-offices/{officeId}/reservations")
    public ResponseEntity<ReservationResponse> create(
            @PathVariable Long officeId,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        ReservationResponse created = reservationService.create(officeId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/reservations/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created); // 201 Created + Location
    }

    /**
     * 특정 오피스의 예약 목록
     * GET /api/shared-offices/{officeId}/reservations
     */
    @GetMapping("/shared-offices/{officeId}/reservations")
    public ResponseEntity<List<ReservationResponse>> listByOffice(@PathVariable Long officeId) {
        return ResponseEntity.ok(reservationService.listByOffice(officeId));
    }

    /**
     * 예약 단건 조회
     * GET /api/reservations/{reservationId}
     */
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> getOne(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.getOne(reservationId));
    }
}
