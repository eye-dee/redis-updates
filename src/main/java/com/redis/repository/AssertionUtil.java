package com.redis.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import redis.clients.jedis.JedisCluster;

public class AssertionUtil {

    private static final List<UUID> UNIQUE_KEY = new CopyOnWriteArrayList<>();

    public static void addKey(UUID value) {
        UNIQUE_KEY.add(value);
    }

    public static void assertAlive(JedisCluster jedisCluster) {
        for (UUID uuid : UNIQUE_KEY) {
            Optional<String> repositoryKey = Optional.ofNullable(jedisCluster.get(uuid.toString()));
            if (!repositoryKey.isPresent()) {
                throw new RuntimeException("unique key = " + uuid + " DOESN'T FOUND");
            }
        }
    }
}
