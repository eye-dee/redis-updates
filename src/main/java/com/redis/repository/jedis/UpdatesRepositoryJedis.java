package com.redis.repository.jedis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.repository.UpdatesRepository;
import com.redis.repository.model.Message;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import redis.clients.jedis.JedisCluster;

public class UpdatesRepositoryJedis implements UpdatesRepository {

    private final JedisCluster jedisCluster;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UpdatesRepositoryJedis(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public long addUpdatesForGroupId(String groupId, String id, List<Message> updates) {
        return updates.stream()
                .map(this::serializeExceptionally)
                .mapToLong(value -> jedisCluster.rpush(generateUniqueId(groupId, id), value))
                .sum();
    }

    @Override
    public List<Message> takeAllMessagesFromGroup(String groupId, String id) {
        long len = jedisCluster.llen(generateUniqueId(groupId, id));
        return jedisCluster.lrange(generateUniqueId(groupId, id), 0, len)
                .stream()
                .map(this::readExceptionally)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> takeMessagesFromGroup(String groupId, String id, int number) {
        return jedisCluster.lrange(generateUniqueId(groupId, id), 0, number)
                .stream()
                .map(this::readExceptionally)
                .collect(Collectors.toList());
    }

    @Override
    public long removeMessagesForGroup(String groupId, String id, long numberOfMessages) {
        return LongStream.range(0, numberOfMessages)
                .mapToObj(i -> jedisCluster.lpop(generateUniqueId(groupId, id)))
                .filter(Objects::nonNull)
                .count();
    }

    private String serializeExceptionally(Message u) {
        try {
            return objectMapper.writeValueAsString(u);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Message readExceptionally(String value) {
        try {
            return objectMapper.readValue(value, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateUniqueId(String groupId, String id) {
        return groupId + "_" + id;
    }
}
