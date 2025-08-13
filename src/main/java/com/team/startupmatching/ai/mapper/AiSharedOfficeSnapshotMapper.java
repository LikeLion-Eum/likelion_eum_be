package com.team.startupmatching.ai.mapper;

import com.team.startupmatching.ai.dto.AiSharedOfficeSnapshot;
import com.team.startupmatching.entity.SharedOffice;

public class AiSharedOfficeSnapshotMapper {

    /** SharedOffice 엔티티 → AI로 보낼 스냅샷 */
    public static AiSharedOfficeSnapshot from(SharedOffice so) {
        return new AiSharedOfficeSnapshot(
                so.getId(),
                nullSafe(so.getName()),
                nullSafe(so.getLocation()),
                nullSafe(so.getDescription()),
                so.getRoomCount(),
                so.getSize(),
                so.getMaxCount()
        );
    }

    private static String nullSafe(String s) {
        return (s == null) ? "" : s;
    }
}
