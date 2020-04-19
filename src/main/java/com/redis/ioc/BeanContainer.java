package com.redis.ioc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.service.RedisMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanContainer {

    private static final Map<String, Object> beans = new ConcurrentHashMap<>();

    static {
        beans.put("objectMapper", new ObjectMapper());
        beans.put("jedisCluster", JedisFactory.jedisCluster());
        beans.put("redisMapper", new RedisMapper(getBean("objectMapper", ObjectMapper.class)));
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return clazz.cast(beans.get(name));
    }
}
