package com.redis.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
@Service
public class UpdatesRepositoryJedis implements UpdatesRepository {

    private final Jedis jedis;

    private final ObjectMapper objectMapper;

    private JavaType listStrings;

    @PostConstruct
    private void initType() {
        listStrings = objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
    }

    @Override
    public Boolean addNewUpdates(String id, Long timestamp, List<String> updates) {
        try {
            String updatesJson = objectMapper.writeValueAsString(updates);
            Map<String, String> updatesMap = Map.of(timestamp.toString(), updatesJson);
            return jedis.hset(id, updatesMap) > 0;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Long, List<String>> getById(String id) {
        Map<String, String> jsonMap = jedis.hgetAll(id);

        return jsonMap.entrySet()
                .stream()
                .map(this::instanceList)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private Pair<Long, List<String>> instanceList(Map.Entry<String, String> entry) {
        try {
            return Pair.of(Long.parseLong(entry.getKey()), objectMapper.readValue(entry.getValue(), listStrings));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean keyExists(String id, Long key) {
        return jedis.hexists(id, key.toString());
    }

    @Override
    public Long deleteTimestamps(String id, List<Long> timestamps) {
        return jedis.hdel(id, timestamps
                .stream()
                .map(Object::toString)
                .toArray(String[]::new));
    }
}
