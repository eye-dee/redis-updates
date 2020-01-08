package com.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.model.MyMsg;
import com.redis.repository.TimestampRepositoryJedis;
import com.redis.repository.UpdatesRepositoryJedis;
import com.redis.service.UpdatesService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class IntegrationTest {

    public static JedisCluster jedis;

    private static ConnectionFactory connectionFactory = new ConnectionFactory();

    @BeforeAll
    public static void initRedis() {
        jedis = connectionFactory.getJedis();

        jedis.getClusterNodes()
                .forEach((name, cluster) -> {
                    Jedis resource = cluster.getResource();
                    Set<String> keys = resource.keys("*");
                    keys.forEach(key -> jedis.del(key));
                });
    }

    private TimestampRepositoryJedis timestampRepositoryJedis
            = new TimestampRepositoryJedis(jedis);

    private final UpdatesRepositoryJedis updatesRepositoryJedis =
            new UpdatesRepositoryJedis(jedis, new ObjectMapper());

    private UpdatesService updatesService = new UpdatesService(
            updatesRepositoryJedis, timestampRepositoryJedis
    );

    @Test
    public void deleteForGroupId() {
        Assertions.assertTrue(
                updatesService.addNewUpdates("dfgi", 1001L, createUpdate()));

        Assertions.assertEquals(updatesRepositoryJedis.getById("dfgi").size(), 1);
        Assertions.assertTrue(updatesService.deleteForGroup("dfgi"));
        Assertions.assertEquals(updatesRepositoryJedis.getById("dfgi").size(), 0);
    }

    @Test
    public void deleteFromGroupIdForTimestamp() {
        Assertions.assertTrue(
                updatesService.addNewUpdates("dfgiat", 1001L, createUpdate()));
        Assertions.assertTrue(
                updatesService.addNewUpdates("dfgiat", 1002L, createUpdate()));

        Assertions.assertEquals(updatesRepositoryJedis.getById("dfgiat").size(), 2);
        Assertions.assertTrue(updatesService.deleteTimestampFromGroup("dfgiat", 1001L));
        Assertions.assertEquals(updatesRepositoryJedis.getById("dfgiat").size(), 1);
    }

    @Test
    void addNewUpdatesNotExist() {
        Assertions.assertTrue(
                updatesService.addNewUpdates("anune", 1001L, createUpdate()));

        Map<Long, List<MyMsg>> expected1 = new HashMap<>();
        expected1.put(1001L, createUpdate());
        Assertions.assertEquals(updatesRepositoryJedis.getById("anune").size(), 1);
        Assertions.assertIterableEquals(updatesRepositoryJedis.getById("anune").get(1001L), createUpdate());

        Assertions.assertTrue(updatesService.addNewUpdates(
                "anune", 1002L, createUpdate()));

        Map<Long, List<MyMsg>> expected2 = new HashMap<>();
        expected2.put(1001L, createUpdate());
        expected2.put(1002L, createUpdate());
        Assertions.assertEquals(updatesRepositoryJedis.getById("anune"), expected2);
    }

    @Test
    void deleteOldUpdates() {
        Assertions.assertTrue(updatesService.addNewUpdates(
                "dou", 1001L, createUpdate()));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "dou", 1002L, createUpdate()));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "dou", 1003L, createUpdate()));

        Assertions.assertEquals(updatesService.deleteOldUpdates("dou"), true);

        Assertions.assertEquals(updatesRepositoryJedis.getById("dou").size(), 1);
        Assertions.assertIterableEquals(updatesRepositoryJedis.getById("dou").get(1003L), createUpdate());
    }

    @Test
    public void getOldest() {
        Assertions.assertTrue(updatesService.addNewUpdates(
                "go1", 11L, createUpdate()));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "go2", 10002L, createUpdate()));

        Assertions.assertTrue(updatesService.addNewUpdates(
                "go3", 10003L, createUpdate()));

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
        Assertions.assertTrue(timestampRepositoryJedis.info() > 0);
    }

    @Test
    public void addNewUpdates() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "anu", 1000L, createUpdates()), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "anu", 1001L, createUpdates()), true);
    }

    @Test
    public void checkKeyExistsTrue() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "cket", 1001L, createUpdates()), true);

        Assertions.assertEquals(updatesRepositoryJedis.keyExists("cket", 1001L), true);
    }

    @Test
    public void checkKeyExistsFalse() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "ckef", 1001L, createUpdates()), true);

        Assertions.assertEquals(updatesRepositoryJedis.keyExists("ckef", 1000L), false);
    }

    @Test
    void getById() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "gbi", 1000L, createUpdates()), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "gbi", 1001L, createUpdates()), true);

        Map<Long, List<MyMsg>> actual = new HashMap<>();
        actual.put(1000L, createUpdates());
        actual.put(1001L, createUpdates());

        Assertions.assertEquals(updatesRepositoryJedis.getById("gbi").size(), 2);
        Assertions.assertEquals(updatesRepositoryJedis.getById("gbi").get(1000L), createUpdates());
        Assertions.assertEquals(updatesRepositoryJedis.getById("gbi").get(1001L), createUpdates());
    }

    @Test
    public void deleteTimestamps() {
        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "dt", 1000L, createUpdates()), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "dt", 1001L, createUpdates()), true);

        Assertions.assertEquals(updatesRepositoryJedis.addNewUpdates(
                "dt", 1002L, createUpdates()), true);

        Assertions.assertEquals(updatesRepositoryJedis.deleteTimestamps(
                "dt", Arrays.asList(1000L, 1001L)), 2L);

        Assertions.assertEquals(updatesRepositoryJedis.getById("dt").size(), 1);
        Assertions.assertIterableEquals(updatesRepositoryJedis.getById("dt").get(1002L), createUpdates());
    }

    private List<MyMsg> createUpdates() {
        return Arrays.asList(new MyMsg("field", "update1"), new MyMsg("field", "update2"));
    }

    private List<MyMsg> createUpdate() {
        return Collections.singletonList(new MyMsg("field", "update"));
    }
}
