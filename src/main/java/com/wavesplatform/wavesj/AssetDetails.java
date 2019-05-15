package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetDetails {
    private final String assetId;
    private final Long issueHeight;
    private final String issuer;
    private final String name;
    private final String description;
    private final Integer decimals;
    private final Boolean reissuable;
    private final Long quantity;
    private final Boolean scripted;
    private final Long minSponsoredAssetFee;

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
