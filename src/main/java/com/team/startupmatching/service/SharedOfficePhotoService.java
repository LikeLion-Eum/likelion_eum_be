package com.team.startupmatching.service;

import com.team.startupmatching.dto.photo.PhotoItemResponse;
import com.team.startupmatching.dto.photo.ReorderRequest;
import com.team.startupmatching.dto.photo.ReplacePhotoResponse;
import com.team.startupmatching.dto.photo.UploadPhotosResponse;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.entity.SharedOfficePhoto;
import com.team.startupmatching.repository.SharedOfficePhotoRepository;
import com.team.startupmatching.repository.SharedOfficeRepository;
import com.team.startupmatching.storage.StoragePort;
import com.team.startupmatching.storage.StorageResult;
import com.team.startupmatching.support.PublicUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharedOfficePhotoService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg","jpeg","png","webp");
    private static final int MAX_FILES_PER_REQUEST = 10;

    private final SharedOfficeRepository officeRepo;
    private final SharedOfficePhotoRepository photoRepo;
    private final StoragePort storagePort;
    private final PublicUrlBuilder publicUrlBuilder;

    /* =========================================================
     * 업로드
     * ======================================================= */
    @Transactional
    public UploadPhotosResponse uploadPhotos(Long officeId, List<MultipartFile> files, List<String> captions) {
        if (files == null || files.isEmpty())
            throw new IllegalArgumentException("업로드 파일이 비어있습니다.");
        if (files.size() > MAX_FILES_PER_REQUEST)
            throw new IllegalArgumentException("요청당 최대 " + MAX_FILES_PER_REQUEST + "장까지 업로드 가능합니다.");

        SharedOffice office = officeRepo.findById(officeId)
                .orElseThrow(() -> new IllegalArgumentException("공유오피스를 찾을 수 없습니다: " + officeId));

        List<SharedOfficePhoto> existing = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
        int nextSeq = existing.isEmpty() ? 0 : existing.get(existing.size() - 1).getSeq() + 1;
        boolean hasMain = existing.stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsMain()));

        List<SharedOfficePhoto> toSave = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile f = files.get(i);
            validateFile(f);

            StorageResult sr = storagePort.upload(officeId.toString(), f);

            SharedOfficePhoto p = SharedOfficePhoto.builder()
                    .sharedOffice(office)
                    .storageKey(sr.key())                 // 절대 URL은 저장하지 않음
                    .seq(nextSeq + i)
                    .isMain(!hasMain && i == 0)           // 첫 업로드면 대표 지정
                    .caption(captions != null && captions.size() > i ? captions.get(i) : null)
                    .build();
            toSave.add(p);
        }

        List<SharedOfficePhoto> saved = photoRepo.saveAll(toSave);

        return UploadPhotosResponse.builder()
                .uploaded(saved.stream().map(this::toDto).collect(Collectors.toList()))
                .build();
    }

    /* =========================================================
     * 조회
     * ======================================================= */
    @Transactional(readOnly = true)
    public List<PhotoItemResponse> listPhotos(Long officeId) {
        return photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PhotoItemResponse getOne(Long officeId, Long photoId) {
        SharedOfficePhoto p = photoRepo.findByIdAndSharedOfficeId(photoId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));
        return toDto(p);
    }

    /* =========================================================
     * 대표 지정 / 정렬 변경 / 단일 seq 변경 / 캡션 수정
     * ======================================================= */
    @Transactional
    public void setMainPhoto(Long officeId, Long photoId) {
        SharedOfficePhoto target = photoRepo.findByIdAndSharedOfficeId(photoId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

        List<SharedOfficePhoto> all = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
        for (SharedOfficePhoto p : all) {
            p.setIsMain(Objects.equals(p.getId(), target.getId()));
        }
        photoRepo.saveAll(all);
    }

    @Transactional
    public void reorderPhotos(Long officeId, ReorderRequest req) {
        if (req == null || req.getOrders() == null || req.getOrders().isEmpty()) {
            throw new IllegalArgumentException("재정렬 목록이 비어있습니다.");
        }

        // 1) 입력 검증 (중복 seq 방지)
        Set<Integer> seqSet = new HashSet<>();
        for (ReorderRequest.OrderItem oi : req.getOrders()) {
            if (!seqSet.add(oi.getSeq())) {
                throw new IllegalArgumentException("중복된 seq가 있습니다: " + oi.getSeq());
            }
            if (oi.getSeq() < 0) {
                throw new IllegalArgumentException("seq는 0 이상이어야 합니다.");
            }
        }

        Map<Long, Integer> desired = req.getOrders().stream()
                .collect(Collectors.toMap(ReorderRequest.OrderItem::getPhotoId, ReorderRequest.OrderItem::getSeq));

        // 2) 전체 로드
        List<SharedOfficePhoto> all = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
        if (all.isEmpty()) return;

        // 3) 1단계: 충돌 방지를 위한 임시 오프셋 부여
        final int OFFSET = 100_000;
        for (SharedOfficePhoto p : all) {
            Integer cur = p.getSeq() == null ? 0 : p.getSeq();
            p.setSeq(cur + OFFSET);
        }
        photoRepo.saveAll(all);
        photoRepo.flush(); // ★ 중간 상태를 DB에 반영해 유니크 충돌 제거

        // 4) 2단계: 최종 seq로 설정
        // 요청에 없는 사진이 있다면 꼬리로 밀어넣기
        int maxProvided = desired.values().stream().max(Integer::compareTo).orElse(-1);
        int tail = maxProvided + 1;

        for (SharedOfficePhoto p : all) {
            Integer want = desired.get(p.getId());
            if (want != null) {
                p.setSeq(want);
            } else {
                p.setSeq(tail++);
            }
        }
        photoRepo.saveAll(all);
        // 필요시 photoRepo.flush();
    }

    @Transactional
    public void updateSequence(Long officeId, Long photoId, Integer seq) {
        if (seq == null || seq < 0) throw new IllegalArgumentException("유효하지 않은 seq 입니다.");
        SharedOfficePhoto p = photoRepo.findByIdAndSharedOfficeId(photoId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));
        p.setSeq(seq);
        photoRepo.save(p);
        normalizeSequences(officeId);
    }

    @Transactional
    public void updateCaption(Long officeId, Long photoId, String caption) {
        SharedOfficePhoto p = photoRepo.findByIdAndSharedOfficeId(photoId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));
        p.setCaption(caption);
        photoRepo.save(p);
    }

    /* =========================================================
     * 사진 교체 (파일 변경)
     * ======================================================= */
    @Transactional
    public ReplacePhotoResponse replacePhoto(Long officeId, Long photoId, MultipartFile file, String caption) {
        validateFile(file);

        SharedOfficePhoto p = photoRepo.findByIdAndSharedOfficeId(photoId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

        // 1) 기존 파일 삭제
        String oldKey = p.getStorageKey();
        if (oldKey != null && !oldKey.isBlank()) {
            storagePort.delete(oldKey);
        }

        // 2) 새 파일 업로드
        StorageResult sr = storagePort.upload(officeId.toString(), file);
        p.setStorageKey(sr.key());

        // 3) 캡션 옵션 변경
        if (caption != null) {
            p.setCaption(caption);
        }

        photoRepo.save(p);

        return ReplacePhotoResponse.builder()
                .photoId(p.getId())
                .url(publicUrlBuilder.build(p.getStorageKey()))
                .caption(p.getCaption())
                .isMain(p.getIsMain())
                .seq(p.getSeq())
                .build();
    }

    /* =========================================================
     * 삭제 (단건/일괄)
     * ======================================================= */
    @Transactional
    public void deletePhoto(Long officeId, Long photoId) {
        SharedOfficePhoto photo = photoRepo.findByIdAndSharedOfficeId(photoId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

        boolean wasMain = Boolean.TRUE.equals(photo.getIsMain());
        String key = photo.getStorageKey();

        photoRepo.delete(photo);
        if (key != null && !key.isBlank()) {
            storagePort.delete(key);
        }

        // 대표 삭제되었으면 첫 사진을 대표로 세팅
        if (wasMain) {
            ensureAnyMainOrSetFirst(officeId);
        }

        // seq 정규화
        normalizeSequences(officeId);
    }

    @Transactional
    public void deletePhotos(Long officeId, List<Long> photoIds) {
        if (photoIds == null || photoIds.isEmpty()) return;

        List<SharedOfficePhoto> targets = photoRepo.findAllById(photoIds).stream()
                .filter(p -> Objects.equals(p.getSharedOffice().getId(), officeId))
                .collect(Collectors.toList());

        boolean mainDeleted = targets.stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsMain()));

        // 파일 먼저 삭제
        for (SharedOfficePhoto p : targets) {
            String key = p.getStorageKey();
            if (key != null && !key.isBlank()) {
                storagePort.delete(key);
            }
        }
        // 메타 삭제
        photoRepo.deleteAll(targets);

        if (mainDeleted) {
            ensureAnyMainOrSetFirst(officeId);
        }
        normalizeSequences(officeId);
    }

    /* =========================================================
     * helpers
     * ======================================================= */

    private void validateFile(MultipartFile f) {
        if (f == null || f.isEmpty())
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        String name = Optional.ofNullable(f.getOriginalFilename()).orElse("");
        int idx = name.lastIndexOf('.');
        String ext = (idx >= 0 && idx < name.length() - 1) ? name.substring(idx + 1).toLowerCase() : null;
        if (ext == null || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않는 확장자입니다. (jpg, jpeg, png, webp)");
        }
    }

    private PhotoItemResponse toDto(SharedOfficePhoto p) {
        String absUrl = publicUrlBuilder.build(p.getStorageKey());
        return PhotoItemResponse.builder()
                .photoId(p.getId())
                .url(absUrl)
                .seq(p.getSeq())
                .isMain(p.getIsMain())
                .caption(p.getCaption())
                .build();
    }

    /** 대표 사진 없으면 첫 번째를 대표로 지정 */
    private void ensureAnyMainOrSetFirst(Long officeId) {
        List<SharedOfficePhoto> remain = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
        if (remain.isEmpty()) return;
        boolean existsMain = remain.stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsMain()));
        if (!existsMain) {
            SharedOfficePhoto first = remain.get(0);
            first.setIsMain(true);
            photoRepo.save(first);
        }
    }

    /** seq 0..n-1로 재정렬(안정적 정렬) */
    private void normalizeSequences(Long officeId) {
        List<SharedOfficePhoto> all = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
        all.sort(Comparator.comparing(SharedOfficePhoto::getSeq, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SharedOfficePhoto::getId));
        for (int i = 0; i < all.size(); i++) {
            all.get(i).setSeq(i);
        }
        photoRepo.saveAll(all);
    }
}
