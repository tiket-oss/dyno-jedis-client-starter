package com.tiket.tix.common.spring.dyno.autoconfigure;

import com.netflix.dyno.connectionpool.Host;
import com.netflix.dyno.connectionpool.HostSupplier;
import com.netflix.dyno.connectionpool.TokenMapSupplier;
import com.netflix.dyno.connectionpool.impl.lb.HostToken;
import com.netflix.dyno.connectionpool.impl.utils.CollectionUtils;
import com.netflix.dyno.jedis.DynoJedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for Netflix Dyno Jedis Client.
 *
 * @author zakyalvan
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@EnableConfigurationProperties(DynoJedisProperties.class)
public class DynoJedisAutoConfiguration {
    private final DynoJedisProperties properties;

    public DynoJedisAutoConfiguration(DynoJedisProperties properties) {
        Assert.notNull(properties, "Dyno jedis properties must be provided");
        this.properties = properties;
    }

    @Bean(destroyMethod = "stopClient")
    @ConditionalOnMissingBean
    DynoJedisClient dynoJedisClient(HostSupplier hostSupplier, TokenMapSupplier tokenMapSupplier) {
        return new DynoJedisClient.Builder()
                .withApplicationName(properties.getApplicationName())
                .withDynomiteClusterName(properties.getDynomiteClusterName())
                .withHostSupplier(hostSupplier)
                .withTokenMapSupplier(tokenMapSupplier)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    HostSupplier hostSupplier() {
        return new CustomHostSupplier(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    TokenMapSupplier tokenMapSupplier() {
        return new CustomTokenMapSupplier(properties);
    }

    /**
     * {@link HostSupplier} based on {@link DynoJedisProperties}.
     */
    static class CustomHostSupplier implements HostSupplier {
        private final DynoJedisProperties properties;

        public CustomHostSupplier(DynoJedisProperties properties) {
            Assert.notNull(properties, "Properties must be provided");
            this.properties = properties;
        }

        @Override
        public List<Host> getHosts() {
            final List<Host> hosts = new ArrayList<>();
            properties.getNodes()
                    .forEach((key, node) -> {
                        Host host = new Host(node.getHostname(), node.getIp(), node.getPort(), node.getRack(), node.getDc(), node.isUp() ? Host.Status.Up : Host.Status.Down);
                        hosts.add(host);
                    });
            return hosts;
        }
    }

    /**
     * {@link TokenMapSupplier} based on {@link DynoJedisProperties}.
     */
    @Slf4j
    static class CustomTokenMapSupplier implements TokenMapSupplier {
        private final DynoJedisProperties properties;

        public CustomTokenMapSupplier(DynoJedisProperties properties) {
            Assert.notNull(properties, "Properties must be provided");
            this.properties = properties;
        }

        @Override
        public List<HostToken> getTokens(Set<Host> activeHosts) {
            Set<HostToken> allTokens = new HashSet<>();
            Set<Host> remainingHosts = new HashSet<>(activeHosts);

            for (Host activeHost : activeHosts) {
                final List<HostToken> hostTokens = new ArrayList<>();
                properties.getNodes()
                        .forEach((key, node) -> {
                            Long token = properties.getTokens().get(key);
                            Host host = new Host(node.getHostname(), node.getIp(), node.getPort(), node.getSport(), node.getRack(), node.getDc(), node.isUp() ? Host.Status.Up : Host.Status.Down, node.getHashtag());
                            hostTokens.add(new HostToken(token, host));
                        });

                for (HostToken hostToken : hostTokens) {
                    allTokens.add(hostToken);
                    remainingHosts.remove(hostToken.getHost());
                }
                if (remainingHosts.size() == 0) {
                    log.info("Received token information for " + allTokens.size() + " hosts. Not querying other hosts");
                    break;
                }
            }
            return new ArrayList<>(allTokens);
        }

        @Override
        public HostToken getTokenForHost(Host host, Set<Host> activeHosts) {
            Set<HostToken> allTokens = new HashSet<>();
            Set<Host> remainingHosts = new HashSet<>(activeHosts);

            for (Host activeHost : activeHosts) {
                final List<HostToken> hostTokens = new ArrayList<>();
                properties.getNodes()
                        .forEach((key, node) -> {
                            Long t = properties.getTokens().get(key);
                            Host h = new Host(node.getHostname(), node.getIp(), node.getPort(), node.getRack(), node.getDc(), node.isUp() ? Host.Status.Up : Host.Status.Down);
                            hostTokens.add(new HostToken(t, h));
                        });

                for (HostToken hostToken : hostTokens) {
                    allTokens.add(hostToken);
                    remainingHosts.remove(hostToken.getHost());
                }
                if (remainingHosts.size() == 0) {
                    log.info("Received token information for " + allTokens.size() + " hosts. Not querying other hosts");
                    break;
                }
            }

            return CollectionUtils.find(allTokens, hostToken -> hostToken.getHost().compareTo(host) == 0);
        }
    }
}
