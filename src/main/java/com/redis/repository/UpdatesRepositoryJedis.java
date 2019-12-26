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
    public Boolean addNewUpdates(Long id, Long timestamp, List<String> updates) {
        try {
            String updatesJson = objectMapper.writeValueAsString(updates);
            Map<String, String> updatesMap = Map.of(timestamp.toString(), updatesJson);
            return jedis.hset(id.toString(), updatesMap) > 0;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Long, List<String>> getById(Long id) {
        Map<String, String> jsonMap = jedis.hgetAll(id.toString());

        return null;
    }

    @Override
    public Boolean keyExists(Long id, Long key) {
        return null;
    }

    @Override
    public Long deleteTimestamps(Long id, List<Long> timestamps) {
        return null;
    }
}
