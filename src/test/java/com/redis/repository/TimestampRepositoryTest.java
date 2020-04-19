package com.redis.repository;

import com.redis.RedisCleaner;
import com.redis.ioc.BeanContainer;
import com.redis.model.TimestampRecord;
import com.redis.repository.jedis.TimestampRepositoryJedis;
import com.redis.service.RedisMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimestampRepositoryTest {

    private final TimestampRepository timestampRepository = new TimestampRepositoryJedis(
            BeanContainer.getBean("jedisCluster", JedisCluster.class),
            BeanContainer.getBean("redisMapper", RedisMapper.class)
    );

    @BeforeAll
    public static void cleanRedis() {
        JedisCluster jedisCluster = BeanContainer.getBean("jedisCluster", JedisCluster.class);
        RedisCleaner.cleanRedis(jedisCluster);
    }

    @Test
    void addNewTimestampRecord() {
        String groupId = "groupId1";
        TimestampRecord record = new TimestampRecord(groupId, "id", "timestamp", 1);

        boolean added = timestampRepository.addNewTimestampRecord(record);

        assertTrue(added);

        Optional<TimestampRecord> actual = timestampRepository.getOldest(groupId);
        assertTrue(actual.isPresent());
        assertEquals(record, actual.get());
    }

    @Test
    void getOldestTestEmpty() {
        String groupId = "groupId2";

        Optional<TimestampRecord> actual = timestampRepository.getOldest(groupId);
        assertFalse(actual.isPresent());
    }

    @Test
    void getOldestTestFull() {
        String groupId = "groupId3";
        TimestampRecord record = new TimestampRecord(groupId, "id", "timestamp", 1);

        timestampRepository.addNewTimestampRecord(record);

        Optional<TimestampRecord> actual = timestampRepository.getOldest(groupId);
        assertTrue(actual.isPresent());
        assertEquals(record, actual.get());
    }

    @Test
    void takeFromHeadTest() {
        String groupId = "groupId4";
        TimestampRecord record1 = new TimestampRecord(groupId, "id1", "timestamp", 1);
        TimestampRecord record2 = new TimestampRecord(groupId, "id2", "timestamp", 1);

        timestampRepository.addNewTimestampRecord(record1);
        timestampRepository.addNewTimestampRecord(record2);

        Optional<TimestampRecord> actual = timestampRepository.getOldest(groupId);
        assertTrue(actual.isPresent());
        assertEquals(record1, actual.get());

        Optional<TimestampRecord> actual1 = timestampRepository.takeFromHead(groupId);
        assertTrue(actual1.isPresent());
        assertEquals(record1, actual1.get());

        Optional<TimestampRecord> actual2 = timestampRepository.takeFromHead(groupId);
        assertTrue(actual2.isPresent());
        assertEquals(record2, actual2.get());
    }

    @Test
    public void takeFromHeadEmpty() {
        Optional<TimestampRecord> actual = timestampRepository.takeFromHead("groupId");
        assertFalse(actual.isPresent());
    }
}
