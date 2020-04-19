package com.redis.ioc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.repository.jedis.TimestampRepositoryJedis;
import com.redis.repository.jedis.UpdateRepositoryJedis;
import com.redis.service.RedisMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import redis.clients.jedis.JedisCluster;

public class BeanContainer {

    private static final Map<String, Object> beans = new ConcurrentHashMap<>();

    static {
        beans.put("objectMapper", new ObjectMapper());
        beans.put("jedisCluster", JedisFactory.jedisCluster());
        beans.put("redisMapper", new RedisMapper(getBean("objectMapper", ObjectMapper.class)));
        beans.put("timestampRepository", new TimestampRepositoryJedis(
                getBean("jedisCluster", JedisCluster.class),
                getBean("redisMapper", RedisMapper.class)
        ));
        beans.put("updateRepository", new UpdateRepositoryJedis(
                getBean("jedisCluster", JedisCluster.class),
                getBean("redisMapper", RedisMapper.class)
        ));
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return clazz.cast(beans.get(name));
    }
}
