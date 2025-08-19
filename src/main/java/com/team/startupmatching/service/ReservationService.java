package com.team.startupmatching.service;

import com.team.startupmatching.dto.ReservationCreateRequest;
import com.team.startupmatching.dto.ReservationResponse;
import com.team.startupmatching.entity.Reservation;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.repository.ReservationRepository;
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
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SharedOfficeRepository sharedOfficeRepository;

    /**
     * 예약 생성
     * 권장: 컨트롤러에서 path로 officeId를 받고, 바디는 ReservationCreateRequest만 받기
     */
    @Transactional
    public ReservationResponse create(Long officeId, ReservationCreateRequest req) {
        SharedOffice office = sharedOfficeRepository.findById(officeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "SharedOffice not found: " + officeId));

        // 전화번호는 숫자만 저장(하이픈/공백 제거)
        String phoneDigits = digitsOnly(req.getReserverPhone());

        Reservation reservation = Reservation.builder()
                .sharedOffice(office)
                .reserverName(req.getReserverName())
                .reserverPhone(phoneDigits)  // 숫자만 저장
                .reserverEmail(req.getReserverEmail())
                .startAt(req.getStartAt())
                .months(req.getMonths())
                .inquiryNote(req.getInquiryNote())
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved);
    }

    /**
     * 특정 오피스의 예약 목록 (시작일시 오름차순)
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> listByOffice(Long officeId) {
        // 오피스 존재 확인(선택). 없다면 404
        sharedOfficeRepository.findById(officeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "SharedOffice not found: " + officeId));

        return reservationRepository.findBySharedOfficeIdOrderByStartAtAsc(officeId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 단건 조회
     */
    @Transactional(readOnly = true)
    public ReservationResponse getOne(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Reservation not found: " + reservationId));
        return toResponse(r);
    }

    // ====== 매핑/포맷 유틸 ======

    private ReservationResponse toResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .sharedOfficeId(r.getSharedOffice() != null ? r.getSharedOffice().getId() : null)
                .reserverName(r.getReserverName())
                .reserverPhone(formatPhone(r.getReserverPhone())) // 보기 좋게 하이픈 포맷
                .reserverEmail(r.getReserverEmail())
                .startAt(r.getStartAt())
                .months(r.getMonths())
                .inquiryNote(r.getInquiryNote())
                .createdAt(r.getCreatedAt())
                .build();
    }

    // 숫자만 남기기
    private String digitsOnly(String v) {
        return v == null ? null : v.replaceAll("\\D", "");
    }

    // 간단 한국형 전화 포맷
    private String formatPhone(String raw) {
        if (raw == null) return null;
        String d = digitsOnly(raw);
        if (d.startsWith("02")) {
            if (d.length() == 9)  return "02-" + d.substring(2,5) + "-" + d.substring(5);
            if (d.length() == 10) return "02-" + d.substring(2,6) + "-" + d.substring(6);
            return raw;
        }
        if (d.length() == 10) return d.substring(0,3)+"-"+d.substring(3,6)+"-"+d.substring(6);
        if (d.length() == 11) return d.substring(0,3)+"-"+d.substring(3,7)+"-"+d.substring(7);
        return raw;
    }
}
