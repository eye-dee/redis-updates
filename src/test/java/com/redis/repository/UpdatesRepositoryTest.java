package com.redis.repository;

import com.redis.AbstractSpringIntegrationTest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class UpdatesRepositoryTest extends AbstractSpringIntegrationTest {

    @Autowired
    private UpdatesRepositoryReactiveImpl updatesRepository;

    @Test
    public void addNewUpdates() {
        StepVerifier.create(updatesRepository.addNewUpdates(1L, 1000L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates(1L, 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void checkKeyExistsTrue() {
        StepVerifier.create(updatesRepository.addNewUpdates(2L, 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.keyExists(2L, 1001L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void checkKeyExistsFalse() {
        StepVerifier.create(updatesRepository.addNewUpdates(3L, 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.keyExists(3L, 1000L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void getById() {
        StepVerifier.create(updatesRepository.addNewUpdates(4L, 1000L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates(4L, 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        Map<Long, List<String>> actual = new HashMap<>();
        actual.put(1000L, Arrays.asList("update1", "update1"));
        actual.put(1001L, Arrays.asList("update1", "update1"));

        StepVerifier.create(updatesRepository.getById(4L))
                .expectNext(actual)
                .verifyComplete();
    }

    @Test
    public void deleteTimestamps() {
        StepVerifier.create(updatesRepository.addNewUpdates(5L, 1000L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates(5L, 1001L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.addNewUpdates(5L, 1002L, Arrays.asList("update1", "update1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(updatesRepository.deleteTimestamps(5L, Arrays.asList(1000L, 1001L)))
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(updatesRepository.getById(5L))
                .expectNext(Map.of(1002L, Arrays.asList("update1", "update1")))
                .verifyComplete();
    }
}
