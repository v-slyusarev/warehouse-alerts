package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageSource;
import reactor.core.publisher.Flux;
import reactor.netty.udp.UdpServer;

import io.netty.channel.socket.DatagramPacket;
import io.netty.buffer.ByteBuf;

public class UdpListener implements SensorMessageSource {
    private final UdpServer udpServer;
    private final TimeProvider timeProvider;
    private final SensorType sensorType;

    public UdpListener(int port, TimeProvider timeProvider, SensorType sensorType) {
        udpServer = UdpServer.create()
                .host("0.0.0.0")
                .port(port);
        this.timeProvider = timeProvider;
        this.sensorType = sensorType;
    }

    @Override
    public Flux<SensorMessage> getMessages() {
        return udpServer.bind()
                .flatMapMany(conn -> conn.inbound().receiveObject()
                        .ofType(DatagramPacket.class)
                        .map(packet -> {
                            ByteBuf content = packet.content();
                            byte[] bytes = new byte[content.readableBytes()];
                            content.getBytes(content.readerIndex(), bytes);
                            return new SensorMessage(
                                    packet.sender(),
                                    timeProvider.getCurrentSystemTimeNano(),
                                    sensorType,
                                    bytes
                            );
                        })
                );

    }
}
