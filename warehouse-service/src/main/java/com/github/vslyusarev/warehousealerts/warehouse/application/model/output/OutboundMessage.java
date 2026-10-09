package com.github.vslyusarev.warehousealerts.warehouse.application.model.output;

public record OutboundMessage(
        byte[] key,
        byte[] payload,
        CorrelationMetadata correlationMetadata
) {

}
