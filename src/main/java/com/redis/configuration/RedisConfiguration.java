package com.redis.configuration;

import redis.clients.jedis.Jedis;

public class RedisConfiguration {

    public Jedis jedis() {
        return new Jedis("localhost", 6379);
    }

}
