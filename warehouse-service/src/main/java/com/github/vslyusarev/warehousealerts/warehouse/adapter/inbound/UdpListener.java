package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSource;
import reactor.core.publisher.Flux;
import reactor.netty.udp.UdpServer;

import io.netty.channel.socket.DatagramPacket;
import io.netty.buffer.ByteBuf;

import java.time.Instant;
import java.time.ZonedDateTime;

public class UdpListener implements SensorMessageSource {
    private final UdpServer udpServer;

    public UdpListener(int port) {
        udpServer = UdpServer.create()
                .host("0.0.0.0")
                .port(port);
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
                                final var addr = packet.sender();
                                final String sender = addr.getAddress().getHostAddress() + ":" + addr.getPort();
                                return new SensorMessage(sender, Instant.now(), bytes);
                        })
                );

    }
}
