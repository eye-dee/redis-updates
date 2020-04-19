package com.redis.ioc;

import java.util.HashSet;
import java.util.Set;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class JedisFactory {

    public static JedisCluster jedisCluster() {
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        jedisClusterNode.add(new HostAndPort("localhost", 7000));
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(16);
        cfg.setMaxIdle(8);
        cfg.setMaxWaitMillis(10000);
        cfg.setTestOnBorrow(true);
        return new JedisCluster(jedisClusterNode, 10000, 1, cfg);
    }

}
