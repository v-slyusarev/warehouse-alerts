package com.github.vslyusarev.warehousealerts.warehouse.streaming.output;

public record OutboundMessage(
        byte[] key,
        byte[] payload,
        CorrelationMetadata correlationMetadata
) {

}
