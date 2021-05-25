package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.common.AssetId;

import java.util.Objects;

public class ReissueAction {
    private final AssetId assetId;
    private final long quantity;
    private final boolean reissuable;

    @JsonCreator
    public ReissueAction(@JsonProperty("assetId") AssetId assetId,
                  @JsonProperty("quantity") long quantity,
                  @JsonProperty("isReissuable") boolean reissuable) {
        this.assetId = assetId;
        this.quantity = quantity;
        this.reissuable = reissuable;
    }

    public AssetId assetId() {
        return assetId;
    }

    public long quantity() {
        return quantity;
    }

    public boolean isReissuable() {
        return reissuable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReissueAction that = (ReissueAction) o;
        return quantity == that.quantity &&
                reissuable == that.reissuable &&
                Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, quantity, reissuable);
    }

    @Override
    public String toString() {
        return "ReissueAction{" +
                "assetId='" + assetId + '\'' +
                ", quantity=" + quantity +
                ", reissuable=" + reissuable +
                '}';
    }
}
