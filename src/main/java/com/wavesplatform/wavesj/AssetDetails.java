package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetDetails {
    private final String assetId;
    private final Long issueHeight;
    private final Long issueTimestamp;
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
                        @JsonProperty("issueTimestamp") final Long issueTimestamp, @JsonProperty("issuer") String issuer,
                        @JsonProperty("name") String name, @JsonProperty("descirption") String description,
                        @JsonProperty("decimals") Integer decimals, @JsonProperty("reissuable") Boolean reissuable,
                        @JsonProperty("quantity") Long quantity, @JsonProperty("scripted") Boolean scripted,
                        @JsonProperty("minSponsoredAssetFee") Long minSponsoredAssetFee) {
        this.assetId = assetId;
        this.issueHeight = issueHeight;
        this.issueTimestamp = issueTimestamp;
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
                        Boolean scripted, Long minSponsoredAssetFee, Object unused, final Long issueTimestamp) {
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
        this.issueTimestamp = issueTimestamp;
    }

    public String getAssetId() {
        return assetId;
    }

    public Long getIssueHeight() {
        return issueHeight;
    }

    public Long getIssueTimestamp() {
        return issueTimestamp;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public Boolean getReissuable() {
        return reissuable;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Boolean getScripted() {
        return scripted;
    }

    public Long getMinSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AssetDetails))
            return false;

        final AssetDetails that = (AssetDetails) o;

        if (getAssetId() != null ? !getAssetId().equals(that.getAssetId()) : that.getAssetId() != null)
            return false;
        if (getIssueHeight() != null ? !getIssueHeight().equals(that.getIssueHeight()) : that.getIssueHeight() != null)
            return false;
        if (getIssueTimestamp() != null ? !getIssueTimestamp().equals(that.getIssueTimestamp()) : that.getIssueTimestamp() != null)
            return false;
        if (getIssuer() != null ? !getIssuer().equals(that.getIssuer()) : that.getIssuer() != null)
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getDecimals() != null ? !getDecimals().equals(that.getDecimals()) : that.getDecimals() != null)
            return false;
        if (getReissuable() != null ? !getReissuable().equals(that.getReissuable()) : that.getReissuable() != null)
            return false;
        if (getQuantity() != null ? !getQuantity().equals(that.getQuantity()) : that.getQuantity() != null)
            return false;
        if (getScripted() != null ? !getScripted().equals(that.getScripted()) : that.getScripted() != null)
            return false;
        return getMinSponsoredAssetFee() != null ? getMinSponsoredAssetFee().equals(that.getMinSponsoredAssetFee()) : that.getMinSponsoredAssetFee() == null;

    }

    @Override
    public int hashCode() {
        int result = getAssetId() != null ? getAssetId().hashCode() : 0;
        result = 31 * result + (getIssueHeight() != null ? getIssueHeight().hashCode() : 0);
        result = 31 * result + (getIssueTimestamp() != null ? getIssueTimestamp().hashCode() : 0);
        result = 31 * result + (getIssuer() != null ? getIssuer().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getDecimals() != null ? getDecimals().hashCode() : 0);
        result = 31 * result + (getReissuable() != null ? getReissuable().hashCode() : 0);
        result = 31 * result + (getQuantity() != null ? getQuantity().hashCode() : 0);
        result = 31 * result + (getScripted() != null ? getScripted().hashCode() : 0);
        result = 31 * result + (getMinSponsoredAssetFee() != null ? getMinSponsoredAssetFee().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AssetDetails{" +
                "assetId='" + assetId + '\'' +
                ", issueHeight=" + issueHeight +
                ", issueTimestamp=" + issueTimestamp +
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
