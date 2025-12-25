package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.BlsSignature;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.transactions.common.Id;

import java.util.Objects;

public class ConflictEndorsement {

    private final int endorserIndex;
    private final Base58String finalizedBlockId;
    private final BlsSignature signature;
    private final int finalizedHeight;

    @JsonCreator
    public ConflictEndorsement(
            @JsonProperty("endorserIndex") int endorserIndex,
            @JsonProperty("finalizedBlockId") Id finalizedBlockId,
            @JsonProperty("finalizedHeight") int finalizedHeight,
            @JsonProperty("signature") BlsSignature signature
    ) {
        this.endorserIndex = endorserIndex;
        this.finalizedBlockId = finalizedBlockId;
        this.finalizedHeight = finalizedHeight;
        this.signature = signature;
    }

    public int getEndorserIndex() {
        return endorserIndex;
    }

    public Base58String getFinalizedBlockId() {
        return finalizedBlockId;
    }

    public int getFinalizedHeight() {
        return finalizedHeight;
    }

    public BlsSignature getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "ConflictEndorsement{" +
                "endorserIndex=" + endorserIndex +
                ", finalizedBlockId=" + finalizedBlockId +
                ", finalizedHeight=" + finalizedHeight +
                ", signature=" + signature +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConflictEndorsement)) return false;
        ConflictEndorsement that = (ConflictEndorsement) o;
        return endorserIndex == that.endorserIndex &&
                Objects.equals(finalizedBlockId, that.finalizedBlockId) &&
                Objects.equals(finalizedHeight, that.finalizedHeight) &&
                Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endorserIndex, finalizedBlockId, finalizedHeight, signature);
    }
}
