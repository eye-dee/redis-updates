package com.redis.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.model.MyMsg;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import redis.clients.jedis.JedisCluster;

public class UpdatesRepositoryJedis implements UpdatesRepository {

    private final JedisCluster jedis;

    private final ObjectMapper objectMapper;

    private final JavaType listOfMessages;

    public UpdatesRepositoryJedis(JedisCluster jedis, ObjectMapper objectMapper) {
        this.jedis = jedis;
        this.objectMapper = objectMapper;
        listOfMessages = objectMapper.getTypeFactory().constructCollectionType(List.class, MyMsg.class);
    }

    @Override
    public Boolean addNewUpdates(String groupId, Long timestamp, List<MyMsg> updates) {
        try {
            String updatesJson = objectMapper.writeValueAsString(updates);
            Map<String, String> updatesMap = new HashMap<>();
            updatesMap.put(timestamp.toString(), updatesJson);
            return jedis.hset(groupId, updatesMap) > 0;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean deleteForGroup(String groupId) {
        return jedis.del(groupId) == 1;
    }

    @Override
    public Map<Long, List<MyMsg>> getById(String id) {
        Map<String, String> jsonMap = jedis.hgetAll(id);

        return jsonMap.entrySet()
                .stream()
                .map(this::instanceList)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<Long, List<MyMsg>> instanceList(Map.Entry<String, String> entry) {
        try {
            return new AbstractMap.SimpleEntry<>(Long.parseLong(entry.getKey()),
                    objectMapper.readValue(entry.getValue(), listOfMessages));
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
