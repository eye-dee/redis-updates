package com.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisMapper {

    private final ObjectMapper objectMapper;

    public RedisMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toRedisEntity(Object entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> T fromString(String value, Class<T> clazz) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
