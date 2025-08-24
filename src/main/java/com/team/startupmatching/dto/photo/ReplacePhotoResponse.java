// ReplacePhotoResponse.java
package com.team.startupmatching.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 교체 후 갱신된 사진 정보를 반환 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplacePhotoResponse {
    private Long photoId;
    private String url;
    private String caption;
    private Boolean isMain;
    private Integer seq;
}
