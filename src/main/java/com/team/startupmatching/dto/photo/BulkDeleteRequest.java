// BulkDeleteRequest.java
package com.team.startupmatching.dto.photo;

import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteRequest {
    private List<Long> photoIds;
}
