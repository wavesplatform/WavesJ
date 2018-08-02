package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WavesJsonMapper extends ObjectMapper {
    public WavesJsonMapper(byte chainId) {
        registerModule(new WavesModule(chainId, this));
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
