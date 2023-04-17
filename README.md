Demo of Redisson's Distributed executor service
===============================================

Usage:
-----------------------------------------------

redis connection config(resources/redisson.yaml):
```yaml
    ---
    singleServerConfig:
      idleConnectionTimeout: 10000
      connectTimeout: 10000
      timeout: 3000
      retryAttempts: 3
      retryInterval: 1500
      password: null
      subscriptionsPerConnection: 5
      clientName: null
      address: "redis://127.0.0.1:6379"
      subscriptionConnectionMinimumIdleSize: 1
      subscriptionConnectionPoolSize: 50
      connectionMinimumIdleSize: 24
      connectionPoolSize: 64
      database: 0
      dnsMonitoringInterval: 5000
    threads: 16
    nettyThreads: 32
    codec: !<org.redisson.codec.Kryo5Codec> {}
    transportMode: "NIO"
```

build:
```
    mvn clean package
```

run worker:

```shell
    java -jar target/redisson-distributed-executor-service-0.0.1-SNAPSHOT.jar worker
```

run producer:
```shell
    java -jar target/redisson-distributed-executor-service-0.0.1-SNAPSHOT.jar producer
```