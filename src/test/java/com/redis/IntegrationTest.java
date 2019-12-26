package com.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.repository.TimestampReactiveWrapper;
import com.redis.repository.TimestampRepositoryJedis;
import com.redis.repository.TimestampRepositoryReactive;
import com.redis.repository.UpdatesReactiveWrapper;
import com.redis.repository.UpdatesRepositoryJedis;
import com.redis.repository.UpdatesRepositoryReactive;
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
import reactor.test.StepVerifier;
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

    private UpdatesRepositoryReactive updatesRepository =
            new UpdatesReactiveWrapper(updatesRepositoryJedis);

    private TimestampRepositoryReactive timestampRepository =
            new TimestampReactiveWrapper(timestampRepositoryJedis);

    private UpdatesService updatesService = new UpdatesService(
            updatesRepository, timestampRepository
    );

    @Test
    void addNewUpdatesNotExist() {
        StepVerifier.create(updatesService.addNewUpdates(
                "anune", 1001L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.getById("anune"))
                .expectNext(Map.of(1001L, Collections.singletonList("update")))
                .verifyComplete();

        StepVerifier.create(timestampRepository.getTimestampForKey("anune"))
                .expectNext(1001L)
                .verifyComplete();

        StepVerifier.create(updatesService.addNewUpdates(
                "anune", 1002L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.getById("anune"))
                .expectNext(Map.of(
                        1001L, Collections.singletonList("update"),
                        1002L, Collections.singletonList("update")
                ))
                .verifyComplete();

        StepVerifier.create(timestampRepository.getTimestampForKey("anune"))
                .expectNext(1001L)
                .verifyComplete();
    }

    @Test
    void deleteOldUpdates() {
        StepVerifier.create(updatesService.addNewUpdates(
                "dou", 1001L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesService.addNewUpdates(
                "dou", 1002L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesService.addNewUpdates(
                "dou", 1003L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesService.deleteOldUpdates("dou"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.getById("dou"))
                .expectNext(Map.of(1003L, Collections.singletonList("update")))
                .verifyComplete();

        StepVerifier.create(timestampRepository.getTimestampForKey("dou"))
                .expectNext(1003L)
                .verifyComplete();
    }

    @Test
    public void getOldest() {
        StepVerifier.create(updatesService.addNewUpdates(
                "go1", 10001L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesService.addNewUpdates(
                "go2", 10002L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesService.addNewUpdates(
                "go3", 10003L, Collections.singletonList("update")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesService.getOldest())
                .expectNext(10003L)
                .verifyComplete();
    }

    @Test
    public void addNewTimestamp() {
        StepVerifier.create(timestampRepository.addNewTimestamp("ant", 1000L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(timestampRepository.addNewTimestamp("ant", 2000L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void overrideOldValue() {
        StepVerifier.create(timestampRepository.overrideOldValue("oov", 1000L))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(timestampRepository.addNewTimestamp("oov", 2000L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(timestampRepository.overrideOldValue("oov", 1000L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void getTimestampForKey() {
        StepVerifier.create(timestampRepository.getTimestampForKey("gtfk"))
                .verifyComplete();

        StepVerifier.create(timestampRepository.addNewTimestamp("gtfk", 2000L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(timestampRepository.getTimestampForKey("gtfk"))
                .expectNext(2000L)
                .verifyComplete();
    }

    @Test
    public void info() {
        StepVerifier.create(timestampRepository.info())
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    public void addNewUpdates() {
        StepVerifier.create(updatesRepository.addNewUpdates("anu", 1000L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates("anu", 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void checkKeyExistsTrue() {
        StepVerifier.create(updatesRepository.addNewUpdates("cket", 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.keyExists("cket", 1001L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void checkKeyExistsFalse() {
        StepVerifier.create(updatesRepository.addNewUpdates("ckef", 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.keyExists("ckef", 1000L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void getById() {
        StepVerifier.create(updatesRepository.addNewUpdates("gbi", 1000L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates("gbi", 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        Map<Long, List<String>> actual = new HashMap<>();
        actual.put(1000L, Arrays.asList("update1", "update1"));
        actual.put(1001L, Arrays.asList("update1", "update1"));

        StepVerifier.create(updatesRepository.getById("gbi"))
                .expectNext(actual)
                .verifyComplete();
    }

    @Test
    public void deleteTimestamps() {
        StepVerifier.create(updatesRepository.addNewUpdates("dt", 1000L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates("dt", 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates("dt", 1002L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.deleteTimestamps("dt", Arrays.asList(1000L, 1001L)))
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(updatesRepository.getById("dt"))
                .expectNext(Map.of(1002L, Arrays.asList("update1", "update1")))
                .verifyComplete();
    }
}
