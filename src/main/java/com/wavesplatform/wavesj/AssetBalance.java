package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.wavesplatform.wavesj.Asset.normalize;

public class AssetBalance {
    public final Long balance;
    public final String assetId;

    @JsonCreator
    public AssetBalance(
            @JsonProperty("balance") Long balance,
            @JsonProperty("assetId") String assetId) {
        this.balance = balance;
        this.assetId = normalize(assetId);
    }

    private AssetBalance(Long balance, String assetId, Object unused) {
        this.balance = balance;
        this.assetId = assetId;
    }


    public Long getAmountAsset() {
        return balance;
    }

    public String getPriceAsset() {
        return assetId;
    }
}
