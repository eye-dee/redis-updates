package com.redis.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import redis.clients.jedis.JedisCluster;

public class AssertionUtil {

    private static final List<String> UNIQUE_KEY = new CopyOnWriteArrayList<>();
    private static final AtomicBoolean CIRCUIT = new AtomicBoolean(false);

    public static void addKey(String value) {
        UNIQUE_KEY.add(value);
    }

    public static void openCircuit() {
        CIRCUIT.set(true);
    }

    public static void assertAlive(JedisCluster jedisCluster) {
        if (CIRCUIT.get()) {
            throw new RuntimeException("some unique keys are missing");
        }

        for (String uniqueKey : UNIQUE_KEY) {
            Optional<String> repositoryKey = Optional.ofNullable(jedisCluster.get(uniqueKey.toString()));
            if (!repositoryKey.isPresent()) {
                throw new RuntimeException("unique key = " + uniqueKey + " DOESN'T FOUND");
            }
        }
    }
}
