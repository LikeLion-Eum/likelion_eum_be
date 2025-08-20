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

    // 파일의 상대 경로 또는 S3 오브젝트 키 (예: uploads/shared-office/{officeId}/{uuid}.jpg)
    @Column(name = "storage_key", nullable = false, length = 512)
    private String storageKey;

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

    /** 프론트로 보낼 상대 URL이 필요할 때 사용: "/uploads/..." 형태 */
    @Transient
    public String getRelativeUrl() {
        if (storageKey == null || storageKey.isBlank()) return null;
        return storageKey.startsWith("/") ? storageKey : "/" + storageKey;
    }
}
