package com.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest extends AbstractSpringIntegrationTest {

    @Test
    void main() {
        assertEquals(2, 2);
    }
}
