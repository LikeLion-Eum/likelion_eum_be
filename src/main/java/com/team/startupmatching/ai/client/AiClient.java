package com.team.startupmatching.ai.client;

import com.team.startupmatching.ai.dto.AiSharedOfficeSnapshot;
import com.team.startupmatching.ai.dto.AiUserSnapshot;
import java.util.List;

public interface AiClient {
    void upsertUsers(List<AiUserSnapshot> items);

    void upsertSharedOffices(List<AiSharedOfficeSnapshot> items);
}