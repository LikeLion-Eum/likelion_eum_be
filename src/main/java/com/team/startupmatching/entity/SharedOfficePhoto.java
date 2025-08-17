package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "shared_office_photo",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_shared_office_photo_office_seq", columnNames = {"shared_office_id", "seq"})
        },
        indexes = {
                @Index(name = "idx_shared_office_photo_office_main", columnList = "shared_office_id,is_main")
        }
)
public class SharedOfficePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → shared_office.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shared_office_id", nullable = false)
    private SharedOffice sharedOffice;

    // 실제 저장 키(로컬 경로 or S3 key)
    @Column(name = "storage_key", nullable = false, length = 512)
    private String storageKey;

    // 프론트가 사용하는 공개 URL
    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;

    // 정렬용(0부터)
    @Column(nullable = false)
    private Integer seq;

    // 대표 사진 여부
    @Column(name = "is_main", nullable = false)
    private Boolean isMain;

    // (선택) 사진 설명
    @Column(length = 255)
    private String caption;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (isMain == null) isMain = false;
        if (seq == null) seq = 0;
    }
}
