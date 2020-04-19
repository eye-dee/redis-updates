package com.redis.repository.jedis;

import com.redis.model.TimestampRecord;
import com.redis.repository.TimestampRepository;
import java.util.Optional;
import redis.clients.jedis.JedisCluster;

public class TimestampRepositoryJedis implements TimestampRepository {

    private final JedisCluster jedis;

    public TimestampRepositoryJedis(JedisCluster jedis) {
        this.jedis = jedis;
    }

    @Override
    public boolean addNewTimestampRecord(TimestampRecord record) {
        return false;
    }

    @Override
    public Optional<TimestampRecord> takeFromHead(String groupId) {
        return Optional.empty();
    }

    @Override
    public Optional<TimestampRecord> getOldest(String groupId) {
        return Optional.empty();
    }
}
