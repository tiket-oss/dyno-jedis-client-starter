package com.tiket.tix.common.spring.dyno.autowire;

import com.netflix.dyno.jedis.DynoJedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Don't forget to set correct properties. See README file for details.
 *
 * @author zakyalvan
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@Slf4j
public class AutoConfiguredDynoJedisIT {

    @Autowired
    private DynoJedisClient dynoJedisClient;

    @Test
    public void testWriteAndReadStringKeyValue() {
        Map<String, String> localCache = new HashMap<>();
        for(int i = 0; i < 100; i++) {
            String key = Integer.toString(i);
            String value = RandomStringUtils.randomAlphabetic(500);
            log.info("Adding key : {}, with value : {}", key, value);
            localCache.put(key, value);
            dynoJedisClient.set(key, value);
        }

        localCache.keySet().forEach(key -> {
            log.info("Check whether key : {}, exists and match local cache value", key);
            assertThat(dynoJedisClient.get(key), equalTo(localCache.get(key)));
            dynoJedisClient.del(key);
        });
    }

    /**
     * Additional testing configuration.
     */
    @EnableAutoConfiguration
    @SpringBootConfiguration
    static class TestingConfiguration {

    }
}
