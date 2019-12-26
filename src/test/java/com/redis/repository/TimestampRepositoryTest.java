package com.redis.repository;

import com.redis.AbstractSpringIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class TimestampRepositoryTest extends AbstractSpringIntegrationTest {

    @Autowired
    TimestampRepositoryReactiveImpl timestampRepository;

    @Test
    public void addNewTimestamp() {
        StepVerifier.create(timestampRepository.addNewTimestamp(6L, 1000L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(timestampRepository.addNewTimestamp(6L, 2000L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void overrideOldValue() {
        StepVerifier.create(timestampRepository.overrideOldValue(7L, 1000L))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(timestampRepository.addNewTimestamp(7L, 2000L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(timestampRepository.overrideOldValue(7L, 1000L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void getTimestampForKey() {
        StepVerifier.create(timestampRepository.getTimestampForKey(8L))
                .verifyComplete();

        StepVerifier.create(timestampRepository.addNewTimestamp(8L, 2000L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(timestampRepository.getTimestampForKey(8L))
                .expectNext(2000L)
                .verifyComplete();
    }

    @Test
    public void info() {
        StepVerifier.create(timestampRepository.info())
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

}
