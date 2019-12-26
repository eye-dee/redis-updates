package com.redis.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@RequiredArgsConstructor
public class TimestampRepositoryJedis implements TimestampRepository {

    private final Jedis jedis;

    @Override
    public Boolean addNewTimestamp(String key, Long value) {
        return "OK".equals(jedis.set("id-" + key, value.toString(),
                SetParams.setParams().nx()
        ));
    }

    @Override
    public Boolean overrideOldValue(String key, Long value) {
        String set = jedis.set("id-" + key, value.toString(),
                SetParams.setParams().xx()
        );
        System.out.println("overrideOldValue = " + set);
        return "OK".equals(set);
    }

    @Override
    public Optional<Long> getTimestampForKey(String key) {
        return Optional.ofNullable(jedis.get("id-" + key))
                .map(Long::parseLong);
    }

    @Override
    public List<Long> getAll() {
        return jedis.mget(jedis.keys("id-*").toArray(new String[0]))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @Override
    public String info() {
        return jedis.info();
    }
}
