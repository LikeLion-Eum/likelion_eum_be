package com.team.startupmatching.ai.listener;

import com.team.startupmatching.ai.service.AiUserSyncService;
import com.team.startupmatching.event.UserChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AiSyncListener {

    private final AiUserSyncService service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserChanged(UserChangedEvent event) {
        // 커밋 완료 후 호출됨 → 여기서 DB 읽어 스냅샷 만들어 전송
        service.syncOne(event.userId());
    }
}
