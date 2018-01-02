package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.wavesplatform.wavesj.Asset.normalize;

public class AssetPair {
    public final String amountAsset;
    public final String priceAsset;

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

    Object toJsonObject() {
        return new AssetPair(Asset.toJsonObject(amountAsset), Asset.toJsonObject(priceAsset), null);
    }
}
