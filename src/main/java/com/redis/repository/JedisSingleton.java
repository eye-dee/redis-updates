package com.redis.repository;

import redis.clients.jedis.JedisCluster;

public enum JedisSingleton {
    JEDIS(ConnectionFactory.getJedis());

    private JedisCluster jedisCluster;

    JedisSingleton(JedisCluster jedis) {
        this.jedisCluster = jedis;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }
}
