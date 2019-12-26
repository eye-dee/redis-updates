package com.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.repository.TimestampRepositoryJedis;
import com.redis.repository.UpdatesRepositoryJedis;
import com.redis.service.UpdatesService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class IntegrationTest {

    public static RedisServer redisServer;

    public static Jedis jedis = new Jedis("localhost", 6379);

    @BeforeAll
    public static void initRedis() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterAll
    public static void stopRedis() {
        redisServer.stop();
    }

    private TimestampRepositoryJedis timestampRepositoryJedis
            = new TimestampRepositoryJedis(jedis);

    private final UpdatesRepositoryJedis updatesRepositoryJedis =
            new UpdatesRepositoryJedis(jedis, new ObjectMapper());

    private UpdatesService updatesService = new UpdatesService(
            updatesRepositoryJedis, timestampRepositoryJedis
    );

    @Test
    void addNewUpdatesNotExist() {
        Assertions.assertTrue(
                updatesService.addNewUpdates("anune", 1001L, Collections.singletonList("update")));

        Assertions.assertEquals(updatesRepositoryJedis.getById("anune"),
                Map.of(1001L, Collections.singletonList("update")));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "anune", 1002L, Collections.singletonList("update")));

        Assertions.assertEquals(updatesRepositoryJedis.getById("anune"),
                Map.of(
                        1001L, Collections.singletonList("update"),
                        1002L, Collections.singletonList("update")
                ));
    }

    @Test
    void deleteOldUpdates() {
        Assertions.assertTrue(updatesService.addNewUpdates(
                "dou", 1001L, Collections.singletonList("update")));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "dou", 1002L, Collections.singletonList("update")));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "dou", 1003L, Collections.singletonList("update")));

        Assertions.assertEquals(updatesService.deleteOldUpdates("dou"), true);

        Assertions.assertEquals(updatesRepositoryJedis.getById("dou"),
                Map.of(1003L, Collections.singletonList("update")));

    }

    @Test
    public void getOldest() {
        Assertions.assertTrue(updatesService.addNewUpdates(
                "go1", 11L, Collections.singletonList("update")));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "go2", 10002L, Collections.singletonList("update")));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "go3", 10003L, Collections.singletonList("update")));

        Assertions.assertEquals(updatesService.getOldest().get(), "go1");
    }

    @Test
    public void addNewTimestamp() {
        Assertions.assertEquals(timestampRepositoryJedis.addNewTimestamp("ant", 1000L), true);

        Assertions.assertEquals(timestampRepositoryJedis.addNewTimestamp("ant", 2000L), false);
    }

    @Test
    public void overrideOldValue() {
        Assertions.assertEquals(timestampRepositoryJedis.overrideOldValue("oov", 1000L), false);

        Assertions.assertEquals(timestampRepositoryJedis.addNewTimestamp("oov", 2000L), true);

        Assertions.assertEquals(timestampRepositoryJedis.overrideOldValue("oov", 1000L), true);
    }

    @Test
    public void info() {
        Assertions.assertNotNull(timestampRepositoryJedis.info());
    }

    @Test
    public void addNewUpdates() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "anu", 1000L, Arrays.asList("update1", "update1")), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "anu", 1001L, Arrays.asList("update1", "update1")), true);
    }

    @Test
    public void checkKeyExistsTrue() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "cket", 1001L, Arrays.asList("update1", "update1")), true);

        Assertions.assertEquals(updatesRepositoryJedis.keyExists("cket", 1001L), true);
    }

    @Test
    public void checkKeyExistsFalse() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "ckef", 1001L, Arrays.asList("update1", "update1")), true);

        Assertions.assertEquals(updatesRepositoryJedis.keyExists("ckef", 1000L), false);
    }

    @Test
    void getById() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "gbi", 1000L, Arrays.asList("update1", "update1")), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "gbi", 1001L, Arrays.asList("update1", "update1")), true);

        Map<Long, List<String>> actual = new HashMap<>();
        actual.put(1000L, Arrays.asList("update1", "update1"));
        actual.put(1001L, Arrays.asList("update1", "update1"));

        Assertions.assertEquals(updatesRepositoryJedis.getById("gbi"), actual);
    }

    @Test
    public void deleteTimestamps() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "dt", 1000L, Arrays.asList("update1", "update1")), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "dt", 1001L, Arrays.asList("update1", "update1")), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "dt", 1002L, Arrays.asList("update1", "update1")), true);

        Assertions.assertEquals(updatesRepositoryJedis.deleteTimestamps(
                "dt", Arrays.asList(1000L, 1001L)), 2L);

        Assertions.assertEquals(updatesRepositoryJedis.getById("dt"),
                Map.of(1002L, Arrays.asList("update1", "update1")));
    }
}
