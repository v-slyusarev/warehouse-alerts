# warehouse_alerts

A monitoring platform built with Java 21, Project Reactor and Apache Kafka.

## Overview
- **[warehouse-service](./warehouse-service/)** - listens to UDP for sensor measurements and publishes sensor events to Kafka
- **[central-service](./central-service)** - processes events from Kafka and emits alerts to stdout
- **[shared](./shared)** - shared contracts, configs, and serizlization/deserialization utilities

## Demo
A local deployment of the monitoring platform can be created using:

``` shell
docker compose up -d
```

### Prerequisites
- Docker image `v-slyusarev/warehouse-service:1.0.0` must be created from [warehouse-service](./warehouse-service)
- Docker image `v-slyusarev/central-service:1.0.0` must be created from [central-service](./central-service)

Refer to these services' README files for instructions on image creation.

### Usage
Sensor messages can be simulated using any utility capable of sending UDP messages, such as netcat. For example:
``` shell
echo -n "sensor_id=t1; value=100" | nc -u 127.0.0.1 3344 -w 0
```
Alerts will appear in the stdout of `warehouse-alerts-central-service-1`. Watch it using:
``` shell
docker logs warehouse-alerts-central-service-1 -f
```
