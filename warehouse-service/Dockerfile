FROM eclipse-temurin:21-jre

WORKDIR /app
COPY build/install/warehouse-service/ /app/

EXPOSE 3344/udp
EXPOSE 3355/udp

ENTRYPOINT ["bin/warehouse-service"]