package com.redis.repository.jedis;

import com.redis.model.TimestampRecord;
import com.redis.model.Update;
import com.redis.repository.TimestampRepository;
import com.redis.service.RedisMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    public List<TimestampRecord> getFirst(String groupId, int n) {
        return Optional.ofNullable(jedis.lrange(groupId, 0, n-1))
                .map(strs -> strs.stream()
                        .map(str -> redisMapper.fromString(str, TimestampRecord.class))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public long deleteFirstForGroup(String group, int n) {
        return Optional.ofNullable(jedis.ltrim(group, n, -1))
                .filter(str -> str.equalsIgnoreCase("ok"))
                .flatMap(str -> Optional.ofNullable(jedis.llen(group)))
                .orElse(0L);
    }

}
