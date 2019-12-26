package com.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisTestConfiguration {

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactoryTest() {
        return new LettuceConnectionFactory("localhost", Integer.parseInt(System.getProperty("redis.port")));
    }

    @Bean
    @Primary
    public Jedis jedisTest() {
        return new Jedis("localhost", Integer.parseInt(System.getProperty("redis.port")));
    }

}
