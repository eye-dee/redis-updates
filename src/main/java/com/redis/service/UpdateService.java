package com.redis.service;

import com.redis.lock.JedisLock;
import com.redis.repository.GroupIdRepository;
import com.redis.repository.InProgressRepository;
import com.redis.repository.UpdatesRepository;
import com.redis.repository.model.Message;
import java.util.List;

public class UpdateService {

    private final GroupIdRepository groupIdRepository;

    private final InProgressRepository inProgressRepository;

    private final UpdatesRepository updatesRepository;

    private final JedisLock jedisLock;

    public UpdateService(GroupIdRepository groupIdRepository, InProgressRepository inProgressRepository,
                         UpdatesRepository updatesRepository, JedisLock jedisLock) {
        this.groupIdRepository = groupIdRepository;
        this.inProgressRepository = inProgressRepository;
        this.updatesRepository = updatesRepository;
        this.jedisLock = jedisLock;
    }

    public boolean handleNewUpdates(String groupId, String id, List<Message> updates) throws InterruptedException {
        boolean acquire = jedisLock.acquire();
        if (!acquire) return false;
        try {
            boolean added = groupIdRepository.addToTheEndForGroup(groupId, id);
            if (added) {
                return updates.size() == updatesRepository.addUpdatesForGroupId(groupId, id, updates);
            }
            return false;
        } finally {
            jedisLock.release();
        }
    }

    public boolean handleUpdatesForGroup(String groupId, int number) throws InterruptedException {
        boolean acquire = jedisLock.acquire();
        if (!acquire) return false;
        try {
            return groupIdRepository.takeFromTheEnd(groupId)
                    .filter(id -> inProgressRepository.takeToProgress(groupId, id))
                    .filter(id -> {
                        List<Message> messages = updatesRepository.takeMessagesFromGroup(groupId, id, number);

                        // do work
                        messages.forEach(System.out::println);

                        return updatesRepository.removeMessagesForGroup(groupId, id, number) == messages.size();
                    })
                    .filter(id -> inProgressRepository.releaseFromProgress(groupId, id))
                    .isPresent();
        } finally {
            jedisLock.release();
        }
    }
}
