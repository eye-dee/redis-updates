package com.redis.repository;

import com.redis.RedisCleaner;
import com.redis.ioc.BeanContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LockRepositoryTest {

    private final LockRepository lockRepository = BeanContainer.getBean("lockRepository", LockRepository.class);

    @BeforeAll
    public static void cleanRedis() {
        JedisCluster jedisCluster = BeanContainer.getBean("jedisCluster", JedisCluster.class);
        RedisCleaner.cleanRedis(jedisCluster);
    }

    @Test
    public void acquireLockSuccessfully() {
        String group = "group1";
        String id = "id1";
        assertTrue(lockRepository.acquireLockForChange(group, id));
    }

    @Test
    public void acquireLockAlreadyTaken() {
        String group = "group2";
        String id = "id2";
        assertTrue(lockRepository.acquireLockForChange(group, id));
        assertFalse(lockRepository.acquireLockForChange(group, id));
        assertFalse(lockRepository.acquireLockForChange(group, id));
        assertFalse(lockRepository.acquireLockForChange(group, id));
    }

    @Test
    public void acquireLockAfterTtl() throws InterruptedException {
        String group = "group3";
        String id = "id3";
        assertTrue(lockRepository.acquireLockForChange(group, id));
        assertFalse(lockRepository.acquireLockForChange(group, id));

        Thread.sleep(6_000);
        assertTrue(lockRepository.acquireLockForChange(group, id));
        assertFalse(lockRepository.acquireLockForChange(group, id));
    }

    @Test
    public void acquireLockForLogicSuccessfully() {
        String group = "group4";
        String id = "id4";
        assertTrue(lockRepository.acquireLockForLogic(group, id));
    }

    @Test
    public void acquireLockForLogicAlreadyTaken() {
        String group = "group5";
        String id = "id5";
        assertTrue(lockRepository.acquireLockForLogic(group, id));
        assertFalse(lockRepository.acquireLockForLogic(group, id));
        assertFalse(lockRepository.acquireLockForLogic(group, id));
        assertFalse(lockRepository.acquireLockForLogic(group, id));
    }

    @Test
    public void acquireLockForLogicAfterTtl() throws InterruptedException {
        String group = "group6";
        String id = "id6";
        assertTrue(lockRepository.acquireLockForLogic(group, id));
        assertFalse(lockRepository.acquireLockForLogic(group, id));

        Thread.sleep(6_000);
        assertTrue(lockRepository.acquireLockForLogic(group, id));
        assertFalse(lockRepository.acquireLockForLogic(group, id));
    }
}
