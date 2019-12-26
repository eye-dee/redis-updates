package com.redis;

import java.io.File;
import org.junit.ClassRule;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.DockerComposeContainer;
import redis.clients.jedis.Jedis;

@Tag("manual")
public abstract class AbstractIntegrationTest {

    @ClassRule
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("redis", 6379);

    @ClassRule
    public static Jedis jedis = new Jedis(
            "localhost",
            environment.getServicePort("redis", 6379)
    );
}
