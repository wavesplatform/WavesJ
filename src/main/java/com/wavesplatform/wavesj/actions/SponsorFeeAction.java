package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.common.AssetId;

import java.util.Objects;

public class SponsorFeeAction {
    private final AssetId assetId;
    private final long minSponsoredAssetFee;

    @JsonCreator
    public SponsorFeeAction(@JsonProperty("assetId") AssetId assetId,
                     @JsonProperty("minSponsoredAssetFee") long minSponsoredAssetFee) {
        this.assetId = assetId;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
    }

    public AssetId assetId() {
        return assetId;
    }

    public long minSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SponsorFeeAction that = (SponsorFeeAction) o;
        return minSponsoredAssetFee == that.minSponsoredAssetFee &&
                Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, minSponsoredAssetFee);
    }

    @Override
    public String toString() {
        return "SponsorFeeAction{" +
                "assetId='" + assetId + '\'' +
                ", minSponsoredAssetFee=" + minSponsoredAssetFee +
                '}';
    }

}
