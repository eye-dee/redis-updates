package com.redis.repository;

import com.redis.model.TimestampRecord;
import com.redis.repository.jedis.TimestampRepositoryJedis;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimestampRepositoryTest {

    private JedisCluster jedis;
    private final TimestampRepository timestampRepository = new TimestampRepositoryJedis(jedis);

    @Test
    void addNewTimestampRecord() {
        String groupId = "groupId";
        TimestampRecord record = new TimestampRecord(groupId, "id", "timestamp", 1);

        boolean added = timestampRepository.addNewTimestampRecord(record);

        assertTrue(added);

        Optional<TimestampRecord> actual = timestampRepository.getOldest(groupId);
        assertTrue(actual.isPresent());
        assertEquals(record, actual.get());
    }

    @Test
    void getOldestTestEmpty() {
        String groupId = "groupId";

        Optional<TimestampRecord> actual = timestampRepository.getOldest(groupId);
        assertFalse(actual.isPresent());
    }

    @Test
    void getOldestTestFull() {
        String groupId = "groupId";
        TimestampRecord record = new TimestampRecord(groupId, "id", "timestamp", 1);

        timestampRepository.addNewTimestampRecord(record);

        Optional<TimestampRecord> actual = timestampRepository.getOldest(groupId);
        assertTrue(actual.isPresent());
        assertEquals(record, actual.get());
    }

    @Test
    void takeFromHeadTest() {
        String groupId = "groupId";
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
}
