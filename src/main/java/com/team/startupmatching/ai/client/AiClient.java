package com.team.startupmatching.ai.client;

import com.team.startupmatching.ai.dto.AiUserSnapshot;
import java.util.List;

public interface AiClient {

    // 단건 업서트: 상대 서버가 "객체 1개"를 본문으로 받을 때
    void upsertOne(AiUserSnapshot snapshot);

    // 일괄 업서트: 상대 서버가 "배열"을 본문으로 받을 때
    void upsertMany(List<AiUserSnapshot> snapshots);
}