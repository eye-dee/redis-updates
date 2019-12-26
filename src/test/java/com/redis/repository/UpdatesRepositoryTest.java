package com.redis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.AbstractSpringIntegrationTest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import redis.clients.jedis.Jedis;

class UpdatesRepositoryTest extends AbstractSpringIntegrationTest {

    private UpdatesRepositoryJedis updatesRepositoryJedis = new UpdatesRepositoryJedis(
            new Jedis("localhost", 6379), new ObjectMapper());

    private UpdatesRepositoryReactive updatesRepository;

    @BeforeEach
    public void setUp() {
        updatesRepository = new UpdatesReactiveWrapper(updatesRepositoryJedis);
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
