package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetPair {
    public final String amountAsset;
    public final String priceAsset;

    @JsonCreator
    private AssetPair(
            @JsonProperty("amountAsset") String amountAsset,
            @JsonProperty("priceAsset") String priceAsset) {
        this.amountAsset = normalize(amountAsset);
        this.priceAsset = normalize(priceAsset);
    }

    private String normalize(String assetId) {
        return assetId == null || assetId.isEmpty() ? Asset.WAVES : assetId;
    }
}
