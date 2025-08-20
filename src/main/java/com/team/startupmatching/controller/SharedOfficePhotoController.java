package com.team.startupmatching.controller;

import com.team.startupmatching.dto.photo.PhotoItemResponse;
import com.team.startupmatching.dto.photo.ReorderRequest;
import com.team.startupmatching.dto.photo.UploadPhotosResponse;
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

    // 업로드 (여러 장)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadPhotosResponse> upload(
            @PathVariable Long officeId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart(value = "captions", required = false) List<String> captions
    ) {
        return ResponseEntity.ok(photoService.uploadPhotos(officeId, files, captions));
    }

    // 목록 조회 (seq ASC)
    @GetMapping
    public ResponseEntity<List<PhotoItemResponse>> list(@PathVariable Long officeId) {
        return ResponseEntity.ok(photoService.listPhotos(officeId));
    }

    // 대표 사진 지정
    @PatchMapping("/{photoId}/main")
    public ResponseEntity<Void> setMain(
            @PathVariable Long officeId,
            @PathVariable Long photoId
    ) {
        photoService.setMainPhoto(officeId, photoId);
        return ResponseEntity.ok().build();
    }

    // 정렬 변경
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorder(
            @PathVariable Long officeId,
            @RequestBody ReorderRequest request
    ) {
        photoService.reorderPhotos(officeId, request);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long officeId,
            @PathVariable Long photoId
    ) {
        photoService.deletePhoto(officeId, photoId);
        return ResponseEntity.ok().build();
    }
}
