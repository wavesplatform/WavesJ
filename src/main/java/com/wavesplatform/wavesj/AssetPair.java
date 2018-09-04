package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.wavesplatform.wavesj.Asset.normalize;

public class AssetPair {
    private String amountAsset;
    private String priceAsset;

    @JsonCreator
    public AssetPair(
            @JsonProperty("amountAsset") String amountAsset,
            @JsonProperty("priceAsset") String priceAsset) {
        this.amountAsset = normalize(amountAsset);
        this.priceAsset = normalize(priceAsset);
    }

    private AssetPair(String amountAsset, String priceAsset, Object unused) {
        this.amountAsset = amountAsset;
        this.priceAsset = priceAsset;
    }

    public String toString() {
        return String.format("AssetPair[%s to %s]", amountAsset, priceAsset);
    }

    public String getAmountAsset() {
        return amountAsset;
    }

    public String getPriceAsset() {
        return priceAsset;
    }
}
