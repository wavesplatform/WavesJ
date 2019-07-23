package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WavesJsonMapper extends ObjectMapper {
    protected final byte chainId;

    public byte getChainId() {
        return chainId;
    }

    public WavesJsonMapper(byte chainId) {
        this.chainId = chainId;
        registerModules();
        configProperties();
    }

    protected void registerModules() {
        registerModule(new WavesModule(chainId, this));
    }

    protected void configProperties() {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
