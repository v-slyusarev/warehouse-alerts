package com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output;

public record OutboundMessage(
        byte[] key,
        byte[] payload,
        CorrelationMetadata correlationMetadata
) {

}
