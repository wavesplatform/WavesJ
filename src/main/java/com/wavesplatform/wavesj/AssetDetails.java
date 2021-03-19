package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;

import java.util.Objects;

@SuppressWarnings("unused")
public class AssetDetails {

    private final AssetId assetId;
    private final int issueHeight;
    private final long issueTimestamp;
    private final Address issuer;
    private final PublicKey issuerPublicKey;
    private final String name;
    private final String description;
    private final int decimals;
    private final boolean reissuable;
    private final long quantity;
    private final boolean scripted;
    private final long minSponsoredAssetFee;
    private final Id originTransactionId;
    private final ScriptDetails scriptDetails;

    @JsonCreator
    public AssetDetails(@JsonProperty("assetId") AssetId assetId,
                        @JsonProperty("issueHeight") int issueHeight,
                        @JsonProperty("issueTimestamp") long issueTimestamp,
                        @JsonProperty("issuer") Address issuer,
                        @JsonProperty("issuerPublicKey") PublicKey issuerPublicKey,
                        @JsonProperty("name") String name,
                        @JsonProperty("description") String description,
                        @JsonProperty("decimals") int decimals,
                        @JsonProperty("reissuable") boolean reissuable,
                        @JsonProperty("quantity") long quantity,
                        @JsonProperty("scripted") boolean scripted,
                        @JsonProperty("minSponsoredAssetFee") long minSponsoredAssetFee,
                        @JsonProperty("originTransactionId") Id originTransactionId,
                        @JsonProperty("scriptDetails") ScriptDetails scriptDetails) {
        this.assetId = Common.notNull(assetId, "AssetId");
        this.issueHeight = issueHeight;
        this.issueTimestamp = issueTimestamp;
        this.issuer = Common.notNull(issuer, "Issuer");
        this.issuerPublicKey = Common.notNull(issuerPublicKey, "IssuerPublicKey");
        this.name = Common.notNull(name, "Name");
        this.description = Common.notNull(description, "Description");
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.quantity = quantity;
        this.scripted = scripted;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
        this.originTransactionId = Common.notNull(originTransactionId, "OriginTransactionId");
        this.scriptDetails = scriptDetails == null ? new ScriptDetails("", 0) : scriptDetails;
    }

    public AssetId assetId() {
        return assetId;
    }

    public int issueHeight() {
        return issueHeight;
    }

    public long issueTimestamp() {
        return issueTimestamp;
    }

    public Address issuer() {
        return issuer;
    }

    public PublicKey issuerPublicKey() {
        return issuerPublicKey;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int decimals() {
        return decimals;
    }

    public boolean isReissuable() {
        return reissuable;
    }

    public long quantity() {
        return quantity;
    }

    public boolean isScripted() {
        return scripted;
    }

    public long minSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    public Id originTransactionId() {
        return originTransactionId;
    }

    public ScriptDetails scriptDetails() {
        return scriptDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetDetails that = (AssetDetails) o;
        return issueHeight == that.issueHeight &&
                issueTimestamp == that.issueTimestamp &&
                decimals == that.decimals &&
                reissuable == that.reissuable &&
                quantity == that.quantity &&
                scripted == that.scripted &&
                minSponsoredAssetFee == that.minSponsoredAssetFee &&
                Objects.equals(assetId, that.assetId) &&
                Objects.equals(issuer, that.issuer) &&
                Objects.equals(issuerPublicKey, that.issuerPublicKey) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(originTransactionId, that.originTransactionId) &&
                Objects.equals(scriptDetails, that.scriptDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, issueHeight, issueTimestamp, issuer, issuerPublicKey, name, description, decimals, reissuable, quantity, scripted, minSponsoredAssetFee, originTransactionId, scriptDetails);
    }

    @Override
    public String toString() {
        return "AssetDetails{" +
                "assetId=" + assetId +
                ", issueHeight=" + issueHeight +
                ", issueTimestamp=" + issueTimestamp +
                ", issuer=" + issuer +
                ", issuerPublicKey=" + issuerPublicKey +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", decimals=" + decimals +
                ", reissuable=" + reissuable +
                ", quantity=" + quantity +
                ", scripted=" + scripted +
                ", minSponsoredAssetFee=" + minSponsoredAssetFee +
                ", originTransactionId=" + originTransactionId +
                ", scriptDetails=" + scriptDetails +
                '}';
    }

}
