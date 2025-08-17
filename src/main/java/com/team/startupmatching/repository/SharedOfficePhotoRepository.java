package com.team.startupmatching.repository;

import com.team.startupmatching.entity.SharedOfficePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedOfficePhotoRepository extends JpaRepository<SharedOfficePhoto, Long> {

    List<SharedOfficePhoto> findBySharedOfficeIdOrderBySeqAsc(Long sharedOfficeId);

    Optional<SharedOfficePhoto> findFirstBySharedOfficeIdAndIsMainTrue(Long sharedOfficeId);

    Optional<SharedOfficePhoto> findByIdAndSharedOfficeId(Long id, Long sharedOfficeId);

    long countBySharedOfficeId(Long sharedOfficeId);

    void deleteBySharedOfficeIdAndId(Long sharedOfficeId, Long id);
}
