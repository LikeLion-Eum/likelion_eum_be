package com.team.startupmatching.ai.listener;

import com.team.startupmatching.ai.service.AiUserSyncService;
import com.team.startupmatching.event.UserChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class AiUserSyncListener {

    private final AiUserSyncService syncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChanged(UserChangedEvent event) {
        if (event == null) return;
        Long userId = event.userId();

        if (userId == null) return;
        syncService.syncOne(userId);
    }
}
