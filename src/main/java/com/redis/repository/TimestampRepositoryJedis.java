package com.redis.repository;

import java.util.Optional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ZAddParams;

public class TimestampRepositoryJedis implements TimestampRepository {

    private static final String KEY = "timestamps";

    private final Jedis jedis;

    public TimestampRepositoryJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public Boolean addNewTimestamp(String key, Long value) {
        return jedis.zadd(KEY, value, key, ZAddParams.zAddParams().nx()) == 1;
    }

    @Override
    public Boolean overrideOldValue(String key, Long value) {
        return jedis.zadd(KEY, value, key, ZAddParams.zAddParams().xx().ch()) == 1;
    }

    @Override
    public Optional<String> getOldest() {
        return jedis.zrangeByScore(KEY, Long.MIN_VALUE, Long.MAX_VALUE, 0, 1)
                .stream()
                .findAny();
    }

    @Override
    public String info() {
        return jedis.info();
    }
}
