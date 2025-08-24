// UpdatePhotoRequest.java
package com.team.startupmatching.dto.photo;

import lombok.Getter;
import lombok.Setter;

/** 사진 메타 부분 수정용 (전부 선택값) */
@Getter @Setter
public class UpdatePhotoRequest {
    private String caption;   // 캡션 수정
    private Boolean isMain;   // true면 대표 지정
    private Integer seq;      // 단건 순서 이동(옵션)
}
