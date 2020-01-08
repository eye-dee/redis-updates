package com.redis.repository;

import java.util.Optional;

public interface GroupIdRepository {

    boolean addToTheEndForGroup(String groupId, String id);

    Optional<String> takeFromTheEnd(String groupId);
}
