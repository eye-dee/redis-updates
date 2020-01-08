package com.redis.repository;

import java.util.Arrays;
import java.util.Optional;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.ZAddParams;

public class TimestampRepositoryJedis implements TimestampRepository {

    private static final String KEY = "timestamps";

    private final JedisCluster jedis;

    public TimestampRepositoryJedis(JedisCluster jedis) {
        this.jedis = jedis;
    }

    @Override
    public boolean addNewTimestamp(String key, Long value) {
        return jedis.zadd(KEY, value, key, ZAddParams.zAddParams().nx()) == 1;
    }

    @Override
    public boolean overrideOldValue(String key, Long value) {
        return jedis.zadd(KEY, value, key, ZAddParams.zAddParams().xx().ch()) == 1;
    }

    @Override
    public boolean addOrOverride(String key, Long value) {
        return jedis.zadd(KEY, value, key, ZAddParams.zAddParams().ch()) == 1;
    }

    @Override
    public Optional<String> getOldest() {
        return jedis.zrangeByScore(KEY, Long.MIN_VALUE, Long.MAX_VALUE, 0, 1)
                .stream()
                .findAny();
    }

    @Override
    public double info() {
        return jedis.getClusterNodes()
                .values()
                .stream()
                .flatMap(jedisPool -> Arrays.stream(jedisPool.getResource().info().split("\n")))
                .filter(str -> str.contains("used_memory_dataset_perc"))
                .map(str -> str.replace("used_memory_dataset_perc:", ""))
                .map(str -> str.replace("%", ""))
                .mapToDouble(Double::parseDouble)
                .average()
                .orElse(0.0);
    }

    @Override
    public boolean deleteForGroup(String groupId) {
        return jedis.zrem(KEY, groupId) == 1;
    }

    @Override
    public boolean deleteTimestamp(Long timestamp) {
        return jedis.zremrangeByRank(KEY, timestamp, timestamp) == 1;
    }
}
