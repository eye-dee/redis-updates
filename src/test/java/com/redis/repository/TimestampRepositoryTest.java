package com.redis.repository;

import com.redis.AbstractSpringIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import redis.clients.jedis.Jedis;

class TimestampRepositoryTest extends AbstractSpringIntegrationTest {

    private TimestampRepositoryJedis timestampRepositoryJedis
            = new TimestampRepositoryJedis(new Jedis("localhost", 6379));

    private TimestampRepositoryReactive timestampRepository;

    @BeforeEach
    public void setUp() {
        timestampRepository = new TimestampReactiveWrapper(timestampRepositoryJedis);
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

}
