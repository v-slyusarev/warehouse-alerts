package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config;

import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.UdpConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound.KafkaConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.sampling.SamplingConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.GeneralConfigProvider;

public interface ConfigProvider extends
        UdpConfigProvider,
        KafkaConfigProvider,
        GeneralConfigProvider,
        SamplingConfigProvider {
}
