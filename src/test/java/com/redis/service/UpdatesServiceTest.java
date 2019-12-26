package com.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.AbstractSpringIntegrationTest;
import com.redis.repository.TimestampReactiveWrapper;
import com.redis.repository.TimestampRepositoryJedis;
import com.redis.repository.TimestampRepositoryReactive;
import com.redis.repository.UpdatesReactiveWrapper;
import com.redis.repository.UpdatesRepositoryJedis;
import com.redis.repository.UpdatesRepositoryReactive;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import redis.clients.jedis.Jedis;

class UpdatesServiceTest extends AbstractSpringIntegrationTest {

    private UpdatesRepositoryReactive updatesRepository = new UpdatesReactiveWrapper(
            new UpdatesRepositoryJedis(new Jedis("localhost", 6379), new ObjectMapper())
    );

    private TimestampRepositoryReactive timestampRepository = new TimestampReactiveWrapper(
            new TimestampRepositoryJedis(new Jedis("localhost", 6379))
    );

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
}
