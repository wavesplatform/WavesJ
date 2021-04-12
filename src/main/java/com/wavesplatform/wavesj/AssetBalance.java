package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.transactions.common.AssetId;

import java.util.Objects;

@SuppressWarnings("unused")
public class AssetBalance {

    private final AssetId assetId;
    private final long balance;
    private final boolean reissuable;
    private final long minSponsoredAssetFee;
    private final long sponsorBalance;
    private final long quantity;
    private final Transaction issueTransaction;

    @JsonCreator
    public AssetBalance(@JsonProperty("assetId") AssetId assetId,
                        @JsonProperty("balance") long balance,
                        @JsonProperty("reissuable") boolean reissuable,
                        @JsonProperty("minSponsoredAssetFee") long minSponsoredAssetFee,
                        @JsonProperty("sponsorBalance") long sponsorBalance,
                        @JsonProperty("quantity") long quantity,
                        @JsonProperty("issueTransaction") Transaction issueTransaction) {
        this.assetId = Common.notNull(assetId, "AssetId");
        this.balance = balance;
        this.reissuable = reissuable;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
        this.sponsorBalance = sponsorBalance;
        this.quantity = quantity;
        this.issueTransaction = issueTransaction;
    }

    public AssetId assetId() {
        return assetId;
    }

    public long balance() {
        return balance;
    }

    public boolean isReissuable() {
        return reissuable;
    }

    public long minSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    public long sponsorBalance() {
        return sponsorBalance;
    }

    public long quantity() {
        return quantity;
    }

    public Transaction issueTransaction() {
        return issueTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetBalance that = (AssetBalance) o;
        return balance == that.balance &&
                reissuable == that.reissuable &&
                minSponsoredAssetFee == that.minSponsoredAssetFee &&
                sponsorBalance == that.sponsorBalance &&
                quantity == that.quantity &&
                assetId.equals(that.assetId) &&
                issueTransaction.equals(that.issueTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, balance, reissuable, minSponsoredAssetFee, sponsorBalance, quantity, issueTransaction);
    }

    @Override
    public String toString() {
        return "AssetBalance{" +
                "assetId=" + assetId.toString() +
                ", balance=" + balance +
                ", reissuable=" + reissuable +
                ", minSponsoredAssetFee=" + minSponsoredAssetFee +
                ", sponsorBalance=" + sponsorBalance +
                ", quantity=" + quantity +
                ", issueTransaction=" + issueTransaction +
                '}';
    }
}
