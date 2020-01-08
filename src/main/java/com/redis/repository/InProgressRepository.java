package com.redis.repository;

public interface InProgressRepository {

    boolean takeToProgress(String groupId, String id);

    boolean releaseFromProgress(String groupId, String id);
}
