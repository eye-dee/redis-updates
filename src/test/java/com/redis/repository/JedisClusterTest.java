package com.redis.repository;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

public class JedisClusterTest {

    private final JedisCluster jedisCluster = JedisSingleton.JEDIS.getJedisCluster();

    @Test
    public void listAllNodes() {
        jedisCluster.getClusterNodes()
                .forEach((ip, node) -> System.out.println("ip = " + ip));
    }

}
