package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import im.mak.waves.transactions.common.AssetId;
import im.mak.waves.transactions.common.Base64String;

import java.util.Objects;

public class IssueAction {
    private final AssetId assetId;
    private final String name;
    private final String description;
    private final long quantity;
    private final int decimals;
    private final boolean reissuable;
    private final Base64String script;
    private final int nonce;

    @JsonCreator
    IssueAction(@JsonProperty("assetId") AssetId assetId,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("quantity") long quantity,
                @JsonProperty("decimals") int decimals,
                @JsonProperty("isReissuable") boolean reissuable,
                @JsonProperty("compiledScript") Base64String compiledScript,
                @JsonProperty("nonce") int nonce) {
        this.assetId = assetId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.script = compiledScript;
        this.nonce = nonce;
    }

    public AssetId assetId() {
        return assetId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public long quantity() {
        return quantity;
    }

    public int decimals() {
        return decimals;
    }

    public boolean isReissuable() {
        return reissuable;
    }

    public Base64String script() {
        return script;
    }

    public int nonce() {
        return nonce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueAction that = (IssueAction) o;
        return quantity == that.quantity &&
                decimals == that.decimals &&
                reissuable == that.reissuable &&
                nonce == that.nonce &&
                Objects.equals(assetId, that.assetId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(script, that.script);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, name, description, quantity, decimals, reissuable, script, nonce);
    }

    @Override
    public String toString() {
        return "IssueAction{" +
                "assetId='" + assetId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", decimals=" + decimals +
                ", reissuable=" + reissuable +
                ", compiledScript='" + script + '\'' +
                ", nonce=" + nonce +
                '}';
    }
}
