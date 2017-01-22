# Spring Cloud Zuul Route Cassandra Store

> A Spring Cloud Cassandra store for Zuul routes.

[![Build Status](https://travis-ci.org/jmnarloch/zuul-route-cassandra-spring-cloud-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/zuul-route-cassandra-spring-cloud-starter)
[![Coverage Status](https://coveralls.io/repos/jmnarloch/zuul-route-cassandra-spring-cloud-starter/badge.svg?branch=master&service=github)](https://coveralls.io/github/jmnarloch/zuul-route-cassandra-spring-cloud-starter?branch=master)

## Features

Extends the Spring Cloud's `DiscoveryClientRouteLocator` with capabilities of loading routes out of the configured Cassandra database.

Instead of configuring your routes through `zuul.routes` like follows:

```yaml
zuul:
  ignoredServices: '*'
  routes:
    resource:
      path: /api/**
      serviceId: rest-service
    oauth2:
      path: /uaa/**
      serviceId: oauth2-service
      stripPrefix: false
```

You can store the routes in Cassandra.

Keep in mind that the other properties except for routes are still relevant.

```yaml
zuul:
  ignoredServices: '*'
  store:
    cassandra:
      enabled: true
```

## Setup

Make sure the version of String Cloud Starter Zuul your project is using is at compatible with 1.2.4.RELEASE which this 
project is extending.

Add the Spring Cloud starter to your project:

```xml
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>zuul-route-cassandra-spring-cloud-starter</artifactId>
  <version>1.1.0-SNAPSHOT</version>
</dependency>
```

Connect to Cassandra and create a keyspace:

```cql
CREATE KEYSPACE IF NOT EXISTS zuul WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

USE zuul;

CREATE TABLE zuul_routes (
    id text,
    path text,
    service_id text,
    url text,
    strip_prefix boolean,
    retryable boolean,
    sensitive_headers set<text>,
    PRIMARY KEY(id)
);
```

Register `CassandraOperations` bean within your application:

```java
@SpringBootApplication
public static class Application {

    @Bean
    public Cluster cluster() {
        return Cluster.builder()
                .addContactPoints(InetAddress.getLoopbackAddress())
                .withPort(9142)
                .build();
    }

    @Bean
    public CassandraOperations cassandraTemplate(Cluster cluster) {
        return new CassandraTemplate(cluster.connect("zuul"));
    }
}
```

Configure the Cassandra to be used for loading the Zuul routes:

```yaml
zuul:
  store:
    cassandra:
      enabled: true
```

Finally enable the Zuul proxy with `@EnableZuulProxyStore` - use this annotation as a replacement for standard `@EnableZuulProxy`:

```java
@EnableZuulProxyStore
@SpringBootApplication
public static class Application {

    ...
}
```

## Upgrading

When upgrading from 1.0.0 to 1.1.0 you will need to alter your Cassandra table to accommodate the new "sensitive_headers"
set added to the underling RouteLocator in Zuul.

```cql
USE zuul;

ALTER TABLE zuul_routes ADD sensitive_headers set<text>;
```

## Properties

```yaml
zuul.store.cassandra.enabled=true# false by default
```

## License

Apache 2.0
