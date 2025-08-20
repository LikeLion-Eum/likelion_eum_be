package com.team.startupmatching.dto.photo;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReorderRequest {
    private List<OrderItem> orders;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItem {
        private Long photoId;
        private Integer seq;
    }
}
