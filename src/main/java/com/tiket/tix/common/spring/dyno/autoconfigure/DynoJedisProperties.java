package com.tiket.tix.common.spring.dyno.autoconfigure;

import com.netflix.dyno.connectionpool.Host;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zakyalvan
 */
@Getter
@ConfigurationProperties(prefix = "tiket.dyno.jedis")
public class DynoJedisProperties implements InitializingBean {
    @Setter
    @NotBlank
    private String applicationName;

    @Setter
    @NotBlank
    private String dynomiteClusterName;

    @NotEmpty
    @NestedConfigurationProperty
    private final Map<String, DynomiteNode> nodes = new HashMap<>();

    @NotEmpty
    @NestedConfigurationProperty
    private final Map<String, Long> tokens = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(nodes.keySet().size() == tokens.keySet().size() && nodes.keySet().containsAll(tokens.keySet()),
                "Nodes and token must have same size");
    }

    @Data
    public static class DynomiteNode {
        @NotBlank
        private String hostname;
        private String ip;
        private int port = Host.DEFAULT_PORT;
        private int sport;
        private String dc;
        private String rack;
        private String hashtag;
        private boolean up = true;
    }
}
