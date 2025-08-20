// com.team.startupmatching.dto.photo.PhotoItemResponse
package com.team.startupmatching.dto.photo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoItemResponse {
    private Long photoId;
    private String url;
    private Integer seq;
    private Boolean isMain;
    private String caption;
}
