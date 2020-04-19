package com.redis.repository.jedis;

import com.redis.model.Update;
import com.redis.repository.UpdateRepository;
import com.redis.service.RedisMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import redis.clients.jedis.JedisCluster;

public class UpdateRepositoryJedis implements UpdateRepository {

    private final JedisCluster jedisCluster;

    private final RedisMapper redisMapper;

    public UpdateRepositoryJedis(JedisCluster jedisCluster, RedisMapper redisMapper) {
        this.jedisCluster = jedisCluster;
        this.redisMapper = redisMapper;
    }

    @Override
    public long addNewUpdatesForGroup(String group, String id, Update update) {
        String entity = redisMapper.toRedisEntity(update);
        return jedisCluster.rpush(generateKey(group, id), entity);
    }

    @Override
    public long deleteElementsFromLeft(String group, String id, int number) {
        String key = generateKey(group, id);
        return Optional.ofNullable(jedisCluster.ltrim(key, number, -1))
                .filter(str -> str.equalsIgnoreCase("ok"))
                .flatMap(str -> Optional.ofNullable(jedisCluster.llen(key)))
                .orElse(0L);
    }

    @Override
    public List<Update> getAllUpdates(String group, String id) {
        String key = generateKey(group, id);
        return Optional.ofNullable(jedisCluster.llen(key))
                .map(len -> jedisCluster.lrange(key, 0, len))
                .map(strs -> strs.stream()
                        .map(str -> redisMapper.fromString(str, Update.class))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private String generateKey(String group, String id) {
        return "updates:group=" + group + ":id=" + id;
    }
}
