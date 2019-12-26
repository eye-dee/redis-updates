package com.redis.configuration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfiguration {

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public Jedis jedis() {
        return new Jedis("localhost", 6379);
    }

    @Bean
    public ReactiveRedisTemplate<Long, Map<Long, List<String>>> reactiveRedisTemplateUpdatesMap(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
            ObjectMapper mapper,
            StringRedisSerializer keySerializer
    ) {
        JavaType longType = mapper.getTypeFactory().constructType(Long.class);
        CollectionType listOfString = mapper.getTypeFactory().constructCollectionType(List.class, String.class);
        MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, longType, listOfString);

        Jackson2JsonRedisSerializer<Map<Long, List<String>>> valueSerializer =
                new Jackson2JsonRedisSerializer<>(mapType);
        Jackson2JsonRedisSerializer<Long> longSerializer = new Jackson2JsonRedisSerializer<>(Long.class);
        Jackson2JsonRedisSerializer<Object> listJacksonSerializer = new Jackson2JsonRedisSerializer<>(listOfString);

        RedisSerializationContext.RedisSerializationContextBuilder<Long, Map<Long, List<String>>> builder =
                RedisSerializationContext.newSerializationContext(longSerializer);
        RedisSerializationContext<Long, Map<Long, List<String>>> context =
                builder.value(valueSerializer)
                        .key(longSerializer)
                        .hashKey(longSerializer)
                        .hashValue(listJacksonSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }

    @Bean
    public StringRedisSerializer keySerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public ReactiveRedisTemplate<String, Long> reactiveRedisTemplateTimestampMap(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
            StringRedisSerializer keySerializer
    ) {
        Jackson2JsonRedisSerializer<Long> valueSerializer =
                new Jackson2JsonRedisSerializer<>(Long.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Long> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, Long> context =
                builder.value(valueSerializer)
                        .key(keySerializer)
                        .value(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }
}
