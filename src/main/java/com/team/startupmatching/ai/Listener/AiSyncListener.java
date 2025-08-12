package com.team.startupmatching.ai.Listener;


import com.team.startupmatching.ai.service.AiUserSyncService;
import com.team.startupmatching.event.UserChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiSyncListener {

    private final AiUserSyncService aiUserSyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserChanged(UserChangedEvent ev) {
        try {
            aiUserSyncService.syncOne(ev.userId());
            log.debug("[AI] after-commit sync ok: userId={}", ev.userId());
        } catch (Exception e) {
            log.warn("[AI] after-commit sync failed: userId={}, err={}", ev.userId(), e.toString());
        }
    }
}