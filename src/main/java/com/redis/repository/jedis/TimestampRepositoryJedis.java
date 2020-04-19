package com.redis.repository.jedis;

import com.redis.model.TimestampRecord;
import com.redis.repository.TimestampRepository;
import com.redis.service.RedisMapper;
import java.util.Optional;
import redis.clients.jedis.JedisCluster;

public class TimestampRepositoryJedis implements TimestampRepository {

    private final JedisCluster jedis;

    private final RedisMapper redisMapper;

    public TimestampRepositoryJedis(JedisCluster jedis, RedisMapper redisMapper) {
        this.jedis = jedis;
        this.redisMapper = redisMapper;
    }

    @Override
    public boolean addNewTimestampRecord(TimestampRecord record) {
        return jedis.rpush(record.getGroupId(), redisMapper.toRedisEntity(record)) == 1;
    }

    @Override
    public Optional<TimestampRecord> takeFromHead(String groupId) {
        return Optional.ofNullable(jedis.lpop(groupId))
                .map(val -> redisMapper.fromString(val, TimestampRecord.class));

    }

    @Override
    public Optional<TimestampRecord> getOldest(String groupId) {
        return Optional.ofNullable(jedis.lindex(groupId, 0))
                .map(val -> redisMapper.fromString(val, TimestampRecord.class));
    }
}
