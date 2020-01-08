package com.redis.repository;

import com.redis.model.MyMsg;
import java.util.List;
import java.util.Map;

public interface UpdatesRepository {

    Boolean addNewUpdates(String id, Long timestamp, List<MyMsg> updates);

    Boolean deleteForGroup(String groupId);

    Map<Long, List<MyMsg>> getById(String id);

    Boolean keyExists(String id, Long key);

    Long deleteTimestamps(String id, List<Long> timestamps);
}
