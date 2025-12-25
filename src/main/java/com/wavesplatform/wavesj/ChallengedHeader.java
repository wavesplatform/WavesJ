package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Base58String;

import java.util.List;
import java.util.Objects;

public class ChallengedHeader {

    private final Base58String headerSignature;
    private final List<Integer> features;
    private final Address generator;
    private final Base58String generatorPublicKey;
    private final long desiredReward;
    private final Base58String stateHash;
    private final FinalizationVoting finalizationVoting;

    @JsonCreator
    public ChallengedHeader(
            @JsonProperty("headerSignature") Base58String headerSignature,
            @JsonProperty("features") List<Integer> features,
            @JsonProperty("generator") Address generator,
            @JsonProperty("generatorPublicKey") Base58String generatorPublicKey,
            @JsonProperty("desiredReward") long desiredReward,
            @JsonProperty("stateHash") Base58String stateHash,
            @JsonProperty("finalizationVoting") FinalizationVoting finalizationVoting
    ) {
        this.headerSignature = headerSignature;
        this.features = features == null ? List.of() : features;
        this.generator = generator;
        this.generatorPublicKey = generatorPublicKey;
        this.desiredReward = desiredReward;
        this.stateHash = stateHash;
        this.finalizationVoting = finalizationVoting;
    }

    public Base58String getHeaderSignature() {
        return headerSignature;
    }

    public List<Integer> getFeatures() {
        return features;
    }

    public Address getGenerator() {
        return generator;
    }

    public Base58String getGeneratorPublicKey() {
        return generatorPublicKey;
    }

    public long getDesiredReward() {
        return desiredReward;
    }

    public Base58String getStateHash() {
        return stateHash;
    }

    public FinalizationVoting getFinalizationVoting() {
        return finalizationVoting;
    }

    @Override
    public String toString() {
        return "ChallengedHeader{" +
                "headerSignature=" + headerSignature +
                ", features=" + features +
                ", generator=" + generator +
                ", generatorPublicKey=" + generatorPublicKey +
                ", desiredReward=" + desiredReward +
                ", stateHash=" + stateHash +
                (finalizationVoting != null ? ", finalizationVoting=" + finalizationVoting : "") +
                '}';

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChallengedHeader)) return false;
        ChallengedHeader that = (ChallengedHeader) o;
        return desiredReward == that.desiredReward &&
                Objects.equals(headerSignature, that.headerSignature) &&
                Objects.equals(features, that.features) &&
                Objects.equals(generator, that.generator) &&
                Objects.equals(generatorPublicKey, that.generatorPublicKey) &&
                Objects.equals(stateHash, that.stateHash) &&
                Objects.equals(finalizationVoting, that.finalizationVoting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerSignature, features, generator, generatorPublicKey, desiredReward, stateHash, finalizationVoting);
    }
}
