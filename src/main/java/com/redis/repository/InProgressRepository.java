package com.redis.repository;

public interface InProgressRepository {

    boolean takeToProgress(String groupId, String id, int timeout);

    boolean releaseFromProgress(String groupId, String id);
}
