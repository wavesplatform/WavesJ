package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WavesJMapper extends ObjectMapper {

    public WavesJMapper() {
        registerModule(new WavesJModule());
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
