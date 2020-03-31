package com.redis.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import redis.clients.jedis.JedisCluster;

public class AssertionUtil {

    private static final Map<String, UUID> UNIQUE_KEY = new ConcurrentHashMap<>();

    public static void addKey(String key, UUID value) {
        UNIQUE_KEY.put(key, value);
    }

    public static void assertAlive(JedisCluster jedisCluster) {
        for (String key : UNIQUE_KEY.keySet()) {
            UUID uuid = UNIQUE_KEY.get(key);
            Optional<String> repositoryKey = Optional.ofNullable(jedisCluster.get(uuid.toString()));
            if (!repositoryKey.isPresent()) {
                throw new RuntimeException("ip key for resource = " + key + " unique key = " + uuid + " DOESN'T FOUND");
            }
        }
    }
}
