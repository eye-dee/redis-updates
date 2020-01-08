package com.redis.repository;

import com.redis.repository.model.Message;
import java.util.List;

public interface UpdatesRepository {

    long addUpdatesForGroupId(String groupId, String id, List<Message> updates);

    List<Message> takeMessagesFromGroup(String groupId, String id, int number);

    long removeMessagesForGroup(String groupId, String id, long numberOfMessages);
}
