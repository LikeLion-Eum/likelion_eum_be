package com.team.startupmatching.repository;

import com.team.startupmatching.entity.SharedOfficePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedOfficePhotoRepository extends JpaRepository<SharedOfficePhoto, Long> {

    // 전체 목록(SEQ 오름차순 보장)
    List<SharedOfficePhoto> findBySharedOfficeIdOrderBySeqAsc(Long sharedOfficeId);

    // 대표 사진 우선(SEQ까지 고려해 결정성 보장)
    Optional<SharedOfficePhoto> findFirstBySharedOfficeIdAndIsMainTrueOrderBySeqAsc(Long sharedOfficeId);

    //대표가 없을 때 첫 번째 항목 fallback
    Optional<SharedOfficePhoto> findFirstBySharedOfficeIdOrderBySeqAsc(Long sharedOfficeId);

    Optional<SharedOfficePhoto> findByIdAndSharedOfficeId(Long id, Long sharedOfficeId);

    long countBySharedOfficeId(Long sharedOfficeId);

    void deleteBySharedOfficeIdAndId(Long sharedOfficeId, Long id);

    // (선택) alias — 필요 없으면 지워도 됨
    List<SharedOfficePhoto> findAllBySharedOfficeIdOrderBySeqAsc(Long sharedOfficeId);

    Optional<SharedOfficePhoto> findFirstBySharedOfficeIdOrderByIsMainDescSeqAsc(Long sharedOfficeId);
}
