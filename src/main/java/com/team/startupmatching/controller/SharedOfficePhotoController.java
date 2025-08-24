package com.team.startupmatching.controller;

import com.team.startupmatching.dto.photo.PhotoItemResponse;
import com.team.startupmatching.dto.photo.ReorderRequest;
import com.team.startupmatching.dto.photo.UploadPhotosResponse;
import com.team.startupmatching.dto.photo.UpdatePhotoRequest;
import com.team.startupmatching.dto.photo.ReplacePhotoResponse;
import com.team.startupmatching.dto.photo.BulkDeleteRequest;
import com.team.startupmatching.service.SharedOfficePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared-offices/{officeId}/photos")
public class SharedOfficePhotoController {

    private final SharedOfficePhotoService photoService;

    /** 여러 장 업로드 (파트명: files / captions[옵션]) */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadPhotosResponse> upload(
            @PathVariable Long officeId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart(value = "captions", required = false) List<String> captions
    ) {
        return ResponseEntity.ok(photoService.uploadPhotos(officeId, files, captions));
    }

    /** 목록 (seq ASC) */
    @GetMapping
    public ResponseEntity<List<PhotoItemResponse>> list(@PathVariable Long officeId) {
        return ResponseEntity.ok(photoService.listPhotos(officeId));
    }

    /** 대표 지정 */
    @PatchMapping("/{photoId}/main")
    public ResponseEntity<Void> setMain(
            @PathVariable Long officeId,
            @PathVariable Long photoId
    ) {
        photoService.setMainPhoto(officeId, photoId);
        return ResponseEntity.noContent().build();
    }

    /** 정렬 변경 (배열 전체 전달) */
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorder(
            @PathVariable Long officeId,
            @RequestBody ReorderRequest request
    ) {
        photoService.reorderPhotos(officeId, request);
        return ResponseEntity.noContent().build();
    }

    /** 부분 수정: 캡션 / 대표 / 단일 seq 변경 */
    @PatchMapping("/{photoId}")
    public ResponseEntity<PhotoItemResponse> updateMeta(
            @PathVariable Long officeId,
            @PathVariable Long photoId,
            @RequestBody UpdatePhotoRequest req
    ) {
        // caption만 변경
        if (req.getCaption() != null) {
            photoService.updateCaption(officeId, photoId, req.getCaption());
        }
        // 대표 지정
        if (Boolean.TRUE.equals(req.getIsMain())) {
            photoService.setMainPhoto(officeId, photoId);
        }
        // 단일 순서 이동(옵션) — 전체 reorder API를 쓰지 않고 특정 사진만 이동시키고 싶을 때
        if (req.getSeq() != null) {
            photoService.updateSequence(officeId, photoId, req.getSeq());
        }
        // 변경 후 최신 메타 반환
        return ResponseEntity.ok(photoService.getOne(officeId, photoId));
    }

    /** 사진 교체: 기존 photoId에 새 파일로 교체 (옵션: 캡션 동시 변경) */
    @PutMapping(value = "/{photoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReplacePhotoResponse> replace(
            @PathVariable Long officeId,
            @PathVariable Long photoId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "caption", required = false) String caption
    ) {
        ReplacePhotoResponse res = photoService.replacePhoto(officeId, photoId, file, caption);
        return ResponseEntity.ok(res);
    }

    /** 단건 삭제 */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long officeId,
            @PathVariable Long photoId
    ) {
        photoService.deletePhoto(officeId, photoId);
        return ResponseEntity.noContent().build();
    }

    /** 일괄 삭제 */
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteBulk(
            @PathVariable Long officeId,
            @RequestBody BulkDeleteRequest req
    ) {
        photoService.deletePhotos(officeId, req.getPhotoIds());
        return ResponseEntity.noContent().build();
    }
}
