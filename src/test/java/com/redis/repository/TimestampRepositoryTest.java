package com.redis.repository;

import com.redis.RedisCleaner;
import com.redis.ioc.BeanContainer;
import com.redis.model.TimestampRecord;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimestampRepositoryTest {

    private final TimestampRepository timestampRepository = BeanContainer.getBean(
            "timestampRepository",
            TimestampRepository.class
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

        List<TimestampRecord> actual = timestampRepository.getFirst(groupId, 1);
        assertFalse(actual.isEmpty());
        assertEquals(record, actual.get(0));
    }

    @Test
    void getOldestTestEmpty() {
        String groupId = "groupId2";

        List<TimestampRecord> actual = timestampRepository.getFirst(groupId, 10);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getOldestTestFull() {
        String groupId = "groupId3";
        TimestampRecord record = new TimestampRecord(groupId, "id", "timestamp", 1);

        timestampRepository.addNewTimestampRecord(record);

        List<TimestampRecord> actual = timestampRepository.getFirst(groupId, 10);
        assertFalse(actual.isEmpty());
        assertEquals(record, actual.get(0));
    }

    @Test
    void takeFromHeadTest() {
        String groupId = "groupId4";
        TimestampRecord record1 = new TimestampRecord(groupId, "id1", "timestamp", 1);
        TimestampRecord record2 = new TimestampRecord(groupId, "id2", "timestamp", 1);

        timestampRepository.addNewTimestampRecord(record1);
        timestampRepository.addNewTimestampRecord(record2);

        List<TimestampRecord> actual = timestampRepository.getFirst(groupId, 10);
        assertFalse(actual.isEmpty());
        assertEquals(record1, actual.get(0));
        assertEquals(record2, actual.get(1));

        assertEquals(1, timestampRepository.deleteFirstForGroup(groupId, 1));

        List<TimestampRecord> actual2 = timestampRepository.getFirst(groupId, 10);
        assertFalse(actual.isEmpty());
        assertEquals(record2, actual2.get(0));
    }

    @Test
    void getOldestTestTwoFull() {
        String groupId = "groupId5";
        TimestampRecord record1 = new TimestampRecord(groupId, "id1", "timestamp", 1);
        TimestampRecord record2 = new TimestampRecord(groupId, "id2", "timestamp", 1);

        timestampRepository.addNewTimestampRecord(record1);
        timestampRepository.addNewTimestampRecord(record2);

        List<TimestampRecord> actual = timestampRepository.getFirst(groupId, 1);
        assertFalse(actual.isEmpty());
        assertEquals(record1, actual.get(0));
        assertEquals(1, actual.size());
    }

    @Test
    public void takeFromHeadEmpty() {
        long actual = timestampRepository.deleteFirstForGroup("groupId", 10);
        assertEquals(0, actual);
    }
}
