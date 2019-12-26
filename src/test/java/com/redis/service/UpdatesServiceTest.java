package com.redis.service;

import com.redis.AbstractSpringIntegrationTest;
import com.redis.repository.TimestampRepositoryReactiveImpl;
import com.redis.repository.UpdatesRepositoryReactiveImpl;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class UpdatesServiceTest extends AbstractSpringIntegrationTest {

    @Autowired
    private UpdatesService updatesService;

    @Autowired
    private UpdatesRepositoryReactiveImpl updatesRepository;

    @Autowired
    private TimestampRepositoryReactiveImpl timestampRepository;

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
