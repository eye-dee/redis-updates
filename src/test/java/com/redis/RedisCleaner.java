package com.redis;

import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class RedisCleaner {

    public static void cleanRedis(JedisCluster jedisCluster) {
        jedisCluster.getClusterNodes()
                .forEach((ip, pool) -> {
                    Jedis resource = pool.getResource();
                    Set<String> keys = resource.keys("*");
                    for (String key : keys) {
                        try {
                            resource.del(key);
                        } catch (RuntimeException ignored) {
                        }
                    }

                    resource.close();
                });
    }
}
