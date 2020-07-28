package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CalculatedFee implements Serializable {
    private final String feeAssetId;
    private final Long feeAmount;

    @JsonCreator
    public CalculatedFee(@JsonProperty("feeAssetId") String feeAssetId, @JsonProperty("feeAmount") Long feeAmount) {
        this.feeAssetId = feeAssetId;
        this.feeAmount = feeAmount;
    }

    public CalculatedFee(final String feeAssetId, final Long feeAmount, Object unused) {
        this.feeAssetId = feeAssetId;
        this.feeAmount = feeAmount;
    }

    public String getFeeAssetId() {
        return feeAssetId;
    }

    public Long getFeeAmount() {
        return feeAmount;
    }

    @Override
    public String toString() {
        return "CalculatedFee{" +
                "feeAssetId='" + feeAssetId + '\'' +
                ", feeAmount=" + feeAmount +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CalculatedFee))
            return false;

        final CalculatedFee that = (CalculatedFee) o;

        if (getFeeAssetId() != null ? !getFeeAssetId().equals(that.getFeeAssetId()) : that.getFeeAssetId() != null)
            return false;
        return getFeeAmount() != null ? getFeeAmount().equals(that.getFeeAmount()) : that.getFeeAmount() == null;
    }

    @Override
    public int hashCode() {
        int result = getFeeAssetId() != null ? getFeeAssetId().hashCode() : 0;
        result = 31 * result + (getFeeAmount() != null ? getFeeAmount().hashCode() : 0);
        return result;
    }
}