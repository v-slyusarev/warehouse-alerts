# warehouse-service
A Java 21 service that listens to UDP for sensor measurements and publishes sensor events to Kafka.

## Installation
To build the Docker image, run:

``` shell
./gradlew installDist && docker build -t v-slyusarev/warehouse-service:1.0.0 .
```

## Dependencies
This service uses the `com.github.vslyusarev.warehousealerts:shared:1.0-SNAPSHOT` library. Build it from [shared](../shared). Refer to the library's README.

## Configuration
The service uses [application.properties](./src/main/resources/application.properties) for configuration.

| Property | Format | Default value | Description |
|-----------|--------|---------------|---------------------|
| `UdpListener.port.TEMPERATURE` | Integer | `3344` | UDP port for temperature sensor |
| `UdpListener.port.HUMIDITY` | Integer | `3355` | UDP port for humidity sensor |
| `warehouse.id` | String | `w1` | The unique identifier of this warehouse |
| `kafka.topic` | String | `warehouse-messages` | The name of the Kafka topic for producing sensor events |
| `kafka.bootstrapServers` | String | `kafka:9092` | Kafka bootstrap server |
