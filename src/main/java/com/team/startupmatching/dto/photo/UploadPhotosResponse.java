// com.team.startupmatching.dto.photo.UploadPhotosResponse
package com.team.startupmatching.dto.photo;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UploadPhotosResponse {
    private List<PhotoItemResponse> uploaded;
}
