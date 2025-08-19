package com.team.startupmatching.repository;

import com.team.startupmatching.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 공유오피스의 예약 목록(시작일시 오름차순)
    List<Reservation> findBySharedOfficeIdOrderByStartAtAsc(Long sharedOfficeId);

    // (선택) 특정 기간에 시작하는 예약이 있는지 체크 — 나중에 겹침 검증에 활용 가능
    boolean existsBySharedOfficeIdAndStartAtBetween(
            Long sharedOfficeId, LocalDateTime from, LocalDateTime to
    );
}
