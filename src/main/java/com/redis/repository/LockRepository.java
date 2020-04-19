package com.redis.repository;

public interface LockRepository {

    boolean acquireLockForChange(String group, String id);

    boolean releaseLockForChange(String group, String id);

    boolean acquireLockForLogic(String group, String id);

    boolean releaseLockForLogic(String group, String id);
}
