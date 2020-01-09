package com.redis.service;

import com.redis.repository.GroupIdRepository;
import com.redis.repository.InProgressRepository;
import com.redis.repository.UpdatesRepository;
import com.redis.repository.model.Message;
import java.util.Collections;
import java.util.List;

public class UpdateService {

    private final GroupIdRepository groupIdRepository;

    private final InProgressRepository inProgressRepository;

    private final UpdatesRepository updatesRepository;

    public UpdateService(GroupIdRepository groupIdRepository, InProgressRepository inProgressRepository,
                         UpdatesRepository updatesRepository) {
        this.groupIdRepository = groupIdRepository;
        this.inProgressRepository = inProgressRepository;
        this.updatesRepository = updatesRepository;
    }

    public boolean handleNewUpdates(String groupId, String id, List<Message> updates) {
        boolean added = groupIdRepository.addToTheEndForGroup(groupId, id);
        if (added) {
            long listSize = updatesRepository.addUpdatesForGroupId(groupId, id, updates);
            return updates.size() < listSize;
        }
        return false;

    }

    public boolean handleUpdatesForGroup(String groupId, int number) {
        return groupIdRepository.takeFromTheEnd(groupId)
                .filter(id -> inProgressRepository.takeToProgress(groupId, id))
                .filter(id -> {
                    List<Message> messages = updatesRepository.takeMessagesFromGroup(groupId, id, number);

                    messages.forEach(System.out::println);

                    return updatesRepository.removeMessagesForGroup(groupId, id, number) == messages.size();
                })
                .filter(id -> inProgressRepository.releaseFromProgress(groupId, id))
                .isPresent();

    }

    public boolean deleteOldUpdates(String groupId, String id, long number) {
        if (number == 0) {
            inProgressRepository.releaseFromProgress(groupId, id);
            return true;
        }
        long result = updatesRepository.removeMessagesForGroup(groupId, id, number);
        boolean release = inProgressRepository.releaseFromProgress(groupId, id);
        return result == number && release;
    }

    public List<Message> readAllUpdates(String groupId, String id) {
        if (inProgressRepository.takeToProgress(groupId, id)) {
            return updatesRepository.takeAllMessagesFromGroup(groupId, id);
        } else {
            return Collections.emptyList();
        }
    }
}
