package com.redis.service;

import com.redis.model.TimestampRecord;
import com.redis.model.Update;
import com.redis.repository.LockRepository;
import com.redis.repository.TimestampRepository;
import com.redis.repository.UpdateRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class UpdateService {

    private final LockRepository lockRepository;

    private final TimestampRepository timestampRepository;

    private final UpdateRepository updateRepository;

    public UpdateService(LockRepository lockRepository, TimestampRepository timestampRepository,
                         UpdateRepository updateRepository) {
        this.lockRepository = lockRepository;
        this.timestampRepository = timestampRepository;
        this.updateRepository = updateRepository;
    }

    public boolean handleNewUpdate(String groupId, String id, Update update) {
        if (lockRepository.acquireLockForChange(groupId, id)) {
            timestampRepository.addNewTimestampRecord(
                    new TimestampRecord(groupId, id, LocalDateTime.now().toString(), update.getKafkaOffset())
            );
            updateRepository.addNewUpdatesForGroup(groupId, id, update);
            lockRepository.releaseLockForChange(groupId, id);
            return true;
        }
        return false;
    }

    public void readUpdates(String groupId) {
        List<TimestampRecord> recs = timestampRepository.getFirst(groupId, 5);
        boolean anyMatched = false;
        int lastNotMatched = 0;
        for (int i = 0; i < recs.size(); i++) {
            TimestampRecord rec = recs.get(i);
            List<Update> updates = updateRepository.getAllUpdates(rec.getGroupId(), rec.getId());
            if (updates.isEmpty()) {
                lastNotMatched = i;
                continue;
            }
            if (updates.get(0).getKafkaOffset() != rec.getKafkaOffset()) {
                lastNotMatched = i;
                continue;
            }
            if (!lockRepository.acquireLockForLogic(rec.getGroupId(), rec.getId())) {
                break;
            }
            updates.forEach(System.out::println);
            if (!lockRepository.acquireLockForChange(rec.getGroupId(), rec.getId())) {
                break;
            }
            updateRepository.deleteElementsFromLeft(rec.getGroupId(), rec.getId(), updates.size());
            timestampRepository.deleteFirstForGroup(groupId, i);
            lockRepository.releaseLockForLogic(rec.getGroupId(), rec.getId());
            lockRepository.releaseLockForChange(rec.getGroupId(), rec.getId());
            anyMatched = true;
        }
        if (lastNotMatched >= 4) {
            timestampRepository.deleteFirstForGroup(groupId, lastNotMatched + 1);
        }
    }
}
