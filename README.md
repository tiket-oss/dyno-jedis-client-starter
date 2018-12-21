# README

## Introduction

Simple starter responsible for auto configuring DynoJedisClient, client for Dynomite.

## Usage

Add into your project

```xml
    <dependency>
        <artifactId>dyno-jedis-client-starter</artifactId>
        <groupId>com.tiket.oss.spring.starters</groupId>
        <version>1.0.0</version>
    </dependency>
```

Configure Dyno connection in ```application.properties```, for this example we have three nodes (as configured in testing servers)

```properties

tiket.dyno.jedis.application-name=sample-application
tiket.dyno.jedis.dynomite-cluster-name=sample-dynomite-cluster

tiket.dyno.jedis.nodes.dyno1.hostname=[hostname1]
tiket.dyno.jedis.nodes.dyno1.ip=[ip-address1]
tiket.dyno.jedis.nodes.dyno1.port=8102
tiket.dyno.jedis.nodes.dyno1.rack=rack1
## Datacenter name
tiket.dyno.jedis.nodes.dyno1.dc=tiket
tiket.dyno.jedis.nodes.dyno1.up=true

tiket.dyno.jedis.nodes.dyno2.hostname=[hostname2]
tiket.dyno.jedis.nodes.dyno2.ip=[ip-address2]
tiket.dyno.jedis.nodes.dyno2.port=8102
tiket.dyno.jedis.nodes.dyno2.rack=rack2
## Datacenter name
tiket.dyno.jedis.nodes.dyno2.dc=tiket
tiket.dyno.jedis.nodes.dyno2.up=true

tiket.dyno.jedis.nodes.dyno3.hostname=[hostname3]
tiket.dyno.jedis.nodes.dyno3.ip=[ip-address3]
tiket.dyno.jedis.nodes.dyno3.port=8102
tiket.dyno.jedis.nodes.dyno3.rack=rack3
## Datacenter name
tiket.dyno.jedis.nodes.dyno3.dc=tiket
tiket.dyno.jedis.nodes.dyno3.up=true


## Configured token for each node.
tiket.dyno.jedis.tokens.dyno1=101134286
tiket.dyno.jedis.tokens.dyno2=101134286
tiket.dyno.jedis.tokens.dyno3=101134286

```

Then, in your code, you can autowire ```com.netflix.dyno.jedis.DynoJedisClient``` type.

Happy Coding...