package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetDetails {
    public final String assetId;
    public final Long issueHeight;
    public final String issuer;
    public final String name;
    public final String description;
    public final Integer decimals;
    public final Boolean reissuable;
    public final Long quantity;
    public final Boolean scripted;
    public final Long minSponsoredAssetFee;

    @JsonCreator
    public AssetDetails(@JsonProperty("assetId") String assetId, @JsonProperty("issueHeight") Long issueHeight,
                        @JsonProperty("issuer") String issuer, @JsonProperty("name") String name,
                        @JsonProperty("descirption") String description, @JsonProperty("decimals") Integer decimals,
                        @JsonProperty("reissuable") Boolean reissuable, @JsonProperty("quantity") Long quantity,
                        @JsonProperty("scripted") Boolean scripted,
                        @JsonProperty("minSponsoredAssetFee") Long minSponsoredAssetFee) {
        this.assetId = assetId;
        this.issueHeight = issueHeight;
        this.issuer = issuer;
        this.name = name;
        this.description = description;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.quantity = quantity;
        this.scripted = scripted;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
    }

    public AssetDetails(String assetId, Long issueHeight, String issuer, String name,
                        String description, Integer decimals, Boolean reissuable, Long quantity,
                        Boolean scripted, Long minSponsoredAssetFee, Object unused) {
        this.assetId = assetId;
        this.issueHeight = issueHeight;
        this.issuer = issuer;
        this.name = name;
        this.description = description;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.quantity = quantity;
        this.scripted = scripted;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
    }

    @Override
    public String toString() {
        return "AssetDetails{" +
                "assetId='" + assetId + '\'' +
                ", issueHeight=" + issueHeight +
                ", issuer='" + issuer + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", decimals=" + decimals +
                ", reissuable=" + reissuable +
                ", quantity=" + quantity +
                ", scripted=" + scripted +
                ", minSponsoredAssetFee=" + minSponsoredAssetFee +
                '}';
    }
}
