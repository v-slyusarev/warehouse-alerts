package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.netty.udp.UdpServer;

import io.netty.channel.socket.DatagramPacket;
import io.netty.buffer.ByteBuf;

public class UdpListener implements SensorMessageSource {
    private final UdpServer udpServer;
    private final TimeProvider timeProvider;
    private final SensorType sensorType;

    private static final Logger log =
            LoggerFactory.getLogger(UdpListener.class);

    public UdpListener(int port, TimeProvider timeProvider, SensorType sensorType) {
        udpServer = UdpServer.create()
                .host("0.0.0.0")
                .port(port);
        this.timeProvider = timeProvider;
        this.sensorType = sensorType;
        log.info("UDP listener for sensor type {} started on port {}", sensorType, port);
    }

    @Override
    public Flux<SensorMessage> getMessages() {
        return udpServer.bind()
                .flatMapMany(conn -> conn.inbound().receiveObject()
                        .ofType(DatagramPacket.class)
                        .map(packet -> {
                            final ByteBuf content = packet.content();
                            final byte[] bytes = new byte[content.readableBytes()];
                            content.getBytes(content.readerIndex(), bytes);
                            return new SensorMessage(
                                    packet.sender(),
                                    timeProvider.getCurrentSystemTimeNano(),
                                    timeProvider.getCurrentInstant(),
                                    sensorType,
                                    bytes
                            );
                        })
                );

    }
}
