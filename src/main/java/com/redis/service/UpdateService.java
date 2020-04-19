package com.redis.service;

import com.redis.model.TimestampRecord;
import com.redis.model.Update;
import com.redis.repository.LockRepository;
import com.redis.repository.TimestampRepository;
import com.redis.repository.UpdateRepository;
import java.time.LocalDateTime;
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

//    public boolean readUpdates(String groupId) {
//        timestampRepository.takeFromHead(groupId)
//                .map(rec -> Pair.of(rec,updateRepository.getAllUpdates(rec.getGroupId(), rec.getId())))
//                .filter(pair -> !pair.getValue().isEmpty())
//                .filter(pair -> pair.getValue().get(0).getKafkaOffset() == pair.getKey().getKafkaOffset())
//                .filter(pair -> lockRepository.acquireLockForLogic(pair.getKey().getGroupId(), pair.getKey().getId()))
//                .map(pair -> {
//                    pair.getValue().forEach(System.out::println);
//                    if (lockRepository.acquireLockForChange(pair.getKey().getGroupId(), pair.getKey().getId())) {
//                        updateRepository.deleteElementsFromLeft(
//                                pair.getKey().getGroupId(),
//                                pair.getKey().getId(),
//                                pair.getValue().size());
//                        lockRepository.releaseLockForChange(pair.getKey().getGroupId(), pair.getKey().getId());
//                    }
//                });
//    }
}
