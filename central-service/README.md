# central-service
A Java 21 service that consumes sensor events from Kafka, processes them based on configurable thresholds, and emits alerts to stdout.

## Installation
To build the Docker image, run:

``` shell
./gradlew installDist && docker build -t v-slyusarev/central-service:1.0.0 .
```

## Dependencies
This service uses the `com.github.vslyusarev.warehousealerts:shared:1.0-SNAPSHOT` library. Build it from [shared](../shared). Refer to the library's README.

## Configuration
The service uses [application.properties][./src/main/resources/application.properties] for configuration.

| Property | Format | Default value | Description |
-----------|--------|---------------|-------------|
| `threshold.TEMPERATURE` | Integer | `35` | Alarm threshold for the temperature sensor, in °C |
| `threshold.HUMIDITY` | Integer | `50` | Alarm threshold for the humidity sensor, in % |
| `kafka.topic` | String | `warehouse-messages` | The name of the Kafka topic for consuming sensor events |
| `kafka.bootstrapServers` | String | `kafka:9092` | Kafka bootstrap server |
| `kafka.consumer.groupId` | String | `central-service` | Kafka consumer group ID |
| `kafka.consumer.groupId` | String | `central-service` | Kafka consumer group ID |
| `EventFilter.maxEventAge` | ISO 8601 duration (PnDTnHnMn.nS) | *(unset)* | *(optional)* If set, then the sensor events older then this value will be ignored. If not set, all events are processed |

