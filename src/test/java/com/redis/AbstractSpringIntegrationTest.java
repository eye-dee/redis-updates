package com.redis;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

@ActiveProfiles("test")
@ExtendWith(value = {SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("spring")
public abstract class AbstractSpringIntegrationTest {

    public static RedisServer redisServer;

    @BeforeAll
    public static void initRedis() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }


    @AfterAll
    public static void stopRedis() {
        redisServer.stop();
    }

}
