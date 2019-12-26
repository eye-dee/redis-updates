package com.redis.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class UpdatesRepositoryJedis implements UpdatesRepository {

    private final Jedis jedis;

    private final ObjectMapper objectMapper;

    @Override
    public Boolean addNewUpdates(String id, Long timestamp, List<String> updates) {
        String updatesJson = null;
        try {
            updatesJson = objectMapper.writeValueAsString(updates);
            Map<String, String> updatesMap = Map.of(timestamp.toString(), updatesJson);
            return jedis.hset(id, updatesMap) > 0;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Long, List<String>> getById(String id) {
        Map<String, String> jsonMap = jedis.hgetAll(id);

        return null;
    }

    @Override
    public Boolean keyExists(String id, Long key) {
        return null;
    }

    @Override
    public Long deleteTimestamps(String id, List<Long> timestamps) {
        return null;
    }
}
