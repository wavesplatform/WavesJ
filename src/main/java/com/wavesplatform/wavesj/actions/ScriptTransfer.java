package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Recipient;
import com.wavesplatform.wavesj.Common;

import java.util.Objects;

public class ScriptTransfer {

    private final Recipient recipient;
    private final long amount;
    private final AssetId assetId;

    @JsonCreator
    ScriptTransfer(@JsonProperty("address") Recipient recipient,
                   @JsonProperty("amount") long amount,
                   @JsonProperty("asset") AssetId assetId) {
        this.recipient = Common.notNull(recipient, "Recipient");
        this.amount = amount;
        this.assetId = assetId == null ? AssetId.WAVES : assetId;
    }

    public Recipient recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
    }

    public AssetId assetId() {
        return assetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptTransfer that = (ScriptTransfer) o;
        return amount == that.amount &&
                Objects.equals(recipient, that.recipient) &&
                Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipient, amount, assetId);
    }

    @Override
    public String toString() {
        return "ScriptTransfer{" +
                "recipient=" + recipient +
                ", amount=" + amount +
                ", assetId=" + assetId +
                '}';
    }
}
