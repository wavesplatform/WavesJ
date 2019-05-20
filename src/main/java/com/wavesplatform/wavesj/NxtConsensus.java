package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NxtConsensus {

    private final long baseTarget;
    private final String generatorSignature;

    @JsonCreator
    public NxtConsensus(
            @JsonProperty("base-target") long baseTarget,
            @JsonProperty("generation-signature") String generatorSignature
    ) {
        this.baseTarget = baseTarget;
        this.generatorSignature = generatorSignature;
    }
    public long getBaseTarget() {
        return baseTarget;
    }

    public String getGeneratorSignature() {
        return generatorSignature;
    }

}
