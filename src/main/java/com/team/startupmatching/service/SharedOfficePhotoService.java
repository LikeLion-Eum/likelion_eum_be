package com.team.startupmatching.service;

import com.team.startupmatching.dto.photo.PhotoItemResponse;
import com.team.startupmatching.dto.photo.ReorderRequest;
import com.team.startupmatching.dto.photo.UploadPhotosResponse;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.entity.SharedOfficePhoto;
import com.team.startupmatching.repository.SharedOfficePhotoRepository;
import com.team.startupmatching.repository.SharedOfficeRepository;
import com.team.startupmatching.storage.StoragePort;
import com.team.startupmatching.storage.StorageResult;
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

    @Transactional
    public UploadPhotosResponse uploadPhotos(Long officeId, List<MultipartFile> files, List<String> captions) {
        if (files == null || files.isEmpty()) throw new IllegalArgumentException("업로드 파일이 비어있습니다.");
        if (files.size() > MAX_FILES_PER_REQUEST) throw new IllegalArgumentException("요청당 최대 " + MAX_FILES_PER_REQUEST + "장까지 업로드 가능합니다.");

        SharedOffice office = officeRepo.findById(officeId)
                .orElseThrow(() -> new IllegalArgumentException("공유오피스를 찾을 수 없습니다: " + officeId));

        List<SharedOfficePhoto> existing = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
        int nextSeq = existing.isEmpty() ? 0 : existing.get(existing.size()-1).getSeq() + 1;
        boolean hasMain = existing.stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsMain()));

        List<SharedOfficePhoto> toSave = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile f = files.get(i);
            validateFile(f);

            StorageResult sr = storagePort.upload(officeId.toString(), f);

            SharedOfficePhoto p = SharedOfficePhoto.builder()
                    .sharedOffice(office)
                    .storageKey(sr.key())
                    .imageUrl(sr.url())
                    .seq(nextSeq + i)                 // max(seq)+1부터 할당 → UNIQUE 충돌 방지
                    .isMain(!hasMain && i == 0)       // 첫 업로드면 대표 지정
                    .caption(captions != null && captions.size() > i ? captions.get(i) : null)
                    .build();
            toSave.add(p);
        }

        List<SharedOfficePhoto> saved = photoRepo.saveAll(toSave);

        return UploadPhotosResponse.builder()
                .uploaded(saved.stream().map(this::toDto).collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<PhotoItemResponse> listPhotos(Long officeId) {
        return photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

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
        if (req == null || req.getOrders() == null || req.getOrders().isEmpty())
            throw new IllegalArgumentException("재정렬 목록이 비어있습니다.");

        // 중복 seq 방지
        Set<Integer> seqSet = new HashSet<>();
        for (ReorderRequest.OrderItem oi : req.getOrders()) {
            if (!seqSet.add(oi.getSeq())) throw new IllegalArgumentException("중복된 seq가 있습니다: " + oi.getSeq());
        }

        Map<Long, Integer> desired = req.getOrders().stream()
                .collect(Collectors.toMap(ReorderRequest.OrderItem::getPhotoId, ReorderRequest.OrderItem::getSeq));

        List<SharedOfficePhoto> all = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
        for (SharedOfficePhoto p : all) {
            if (desired.containsKey(p.getId())) p.setSeq(desired.get(p.getId()));
        }
        photoRepo.saveAll(all);
    }

    @Transactional
    public void deletePhoto(Long officeId, Long photoId) {
        SharedOfficePhoto photo = photoRepo.findByIdAndSharedOfficeId(photoId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

        boolean wasMain = Boolean.TRUE.equals(photo.getIsMain());
        String key = photo.getStorageKey();

        photoRepo.delete(photo);
        storagePort.delete(key);

        if (wasMain) {
            List<SharedOfficePhoto> remain = photoRepo.findBySharedOfficeIdOrderBySeqAsc(officeId);
            if (!remain.isEmpty()) {
                SharedOfficePhoto first = remain.get(0);
                first.setIsMain(true);
                photoRepo.save(first);
            }
        }
        // 필요하면 여기에서 seq를 0..N으로 재정렬 가능(필수는 아님)
    }

    /* helper */

    private void validateFile(MultipartFile f) {
        if (f == null || f.isEmpty()) throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        String name = Optional.ofNullable(f.getOriginalFilename()).orElse("");
        int idx = name.lastIndexOf('.');
        String ext = (idx >= 0 && idx < name.length() - 1) ? name.substring(idx + 1).toLowerCase() : null;
        if (ext == null || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않는 확장자입니다. (jpg, jpeg, png, webp)");
        }
        // 필요 시 Content-Type 검증 추가 가능
    }

    private PhotoItemResponse toDto(SharedOfficePhoto p) {
        return PhotoItemResponse.builder()
                .photoId(p.getId())
                .url(p.getImageUrl())
                .seq(p.getSeq())
                .isMain(p.getIsMain())
                .caption(p.getCaption())
                .build();
    }
}
