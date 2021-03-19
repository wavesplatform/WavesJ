package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.common.AssetId;

import java.util.Objects;

public class BurnAction {
    private final AssetId assetId;
    private final long amount;

    @JsonCreator
    BurnAction(@JsonProperty("assetId") AssetId assetId,
               @JsonProperty("amount") long amount) {
        this.assetId = assetId;
        this.amount = amount;
    }

    public AssetId assetId() {
        return assetId;
    }

    public long amount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BurnAction that = (BurnAction) o;
        return amount == that.amount &&
                Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, amount);
    }

    @Override
    public String toString() {
        return "BurnAction{" +
                "assetId='" + assetId + '\'' +
                ", amount=" + amount +
                '}';
    }

}
