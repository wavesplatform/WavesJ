package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.BlsSignature;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FinalizationVoting {

    private final List<Integer> endorserIndexes;
    private final BlsSignature aggregatedEndorsementSignature;
    private final List<ConflictEndorsement> conflictEndorsements;
    private final int finalizedHeight;

    @JsonCreator
    public FinalizationVoting(
            @JsonProperty("endorserIndexes") List<Integer> endorserIndexes,
            @JsonProperty("aggregatedEndorsementSignature") BlsSignature aggregatedEndorsementSignature,
            @JsonProperty("conflictEndorsements") List<ConflictEndorsement> conflictEndorsements,
            @JsonProperty("finalizedHeight") int finalizedHeight
    ) {
        this.endorserIndexes = endorserIndexes == null ? Collections.emptyList() : endorserIndexes;
        this.aggregatedEndorsementSignature = aggregatedEndorsementSignature;
        this.finalizedHeight = finalizedHeight;
        this.conflictEndorsements = conflictEndorsements == null ? Collections.emptyList() : conflictEndorsements;
    }

    public List<Integer> getEndorserIndexes() {
        return endorserIndexes;
    }

    public BlsSignature getAggregatedEndorsementSignature() {
        return aggregatedEndorsementSignature;
    }

    public int getFinalizedHeight() {
        return finalizedHeight;
    }

    public List<ConflictEndorsement> getConflictEndorsements() {
        return conflictEndorsements;
    }

    @Override
    public String toString() {
        return "FinalizationVoting{" +
                "endorserIndexes=" + endorserIndexes +
                ", aggregatedEndorsementSignature=" + aggregatedEndorsementSignature +
                ", finalizedHeight=" + finalizedHeight +
                (!conflictEndorsements.isEmpty() ? ", conflictEndorsements=" + conflictEndorsements : "") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinalizationVoting that = (FinalizationVoting) o;
        return Objects.equals(endorserIndexes, that.endorserIndexes) &&
                Objects.equals(aggregatedEndorsementSignature, that.aggregatedEndorsementSignature) &&
                Objects.equals(finalizedHeight, that.finalizedHeight) &&
                Objects.equals(conflictEndorsements, that.conflictEndorsements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endorserIndexes, aggregatedEndorsementSignature, finalizedHeight, conflictEndorsements);
    }
}
