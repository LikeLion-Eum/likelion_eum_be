package com.team.startupmatching.ai.listener;

import com.team.startupmatching.ai.service.AiIncubationCenterSyncService;
import com.team.startupmatching.event.IncubationCenterChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class AiIncubationCenterSyncListener {

    private final AiIncubationCenterSyncService syncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChanged(IncubationCenterChangedEvent event) {
        if (event == null || event.incubationCenterId() == null) return;
        syncService.syncOne(event.incubationCenterId());
    }
}
