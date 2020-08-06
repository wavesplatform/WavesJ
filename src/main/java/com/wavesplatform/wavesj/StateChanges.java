package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import im.mak.waves.transactions.common.AssetId;
import im.mak.waves.transactions.common.Recipient;
import im.mak.waves.transactions.data.DataEntry;

import java.util.*;

@SuppressWarnings("unused")
public class StateChanges {

    private final List<DataEntry> data;
    private final List<ScriptTransfer> transfers;
    private final List<IssueAction> issues;
    private final List<ReissueAction> reissues;
    private final List<BurnAction> burns;
    private final List<SponsorFeeAction> sponsorFees;
    private final Error error;

    @JsonCreator
    public StateChanges(@JsonProperty("data") List<DataEntry> data,
                 @JsonProperty("transfers") List<ScriptTransfer> transfers,
                 @JsonProperty("issues") List<IssueAction> issues,
                 @JsonProperty("reissues") List<ReissueAction> reissues,
                 @JsonProperty("burns") List<BurnAction> burns,
                 @JsonProperty("sponsorFees") List<SponsorFeeAction> sponsorFees,
                 @JsonProperty("error") Error error) {
        this.data = data != null ? data : new ArrayList<>();
        this.transfers = transfers != null ? transfers : new ArrayList<>();
        this.issues = issues != null ? issues : new ArrayList<>();
        this.reissues = reissues != null ? reissues : new ArrayList<>();
        this.burns = burns != null ? burns : new ArrayList<>();
        this.sponsorFees = sponsorFees != null ? sponsorFees : new ArrayList<>();
        this.error = error != null ? error : new Error(0, "");
    }

    /**
     * Returns collection of account's {@link DataEntry DataEntries} which were modified after applying transaction
     * @return collection of {@link DataEntry DataEntries} or empty list
     */
    public List<DataEntry> data() {
        return data;
    }

    /**
     * Returns collection of output transfers which were triggered after applying transaction. Currently such transfers
     * are supported by ScriptInvocationTransaction only
     * @return collection of transfers or empty list
     */
    public List<ScriptTransfer> transfers() {
        return transfers;
    }

    /**
     * Returns collection of assets which were issued after applying transaction.
     * @return collection of issued assets or empty list
     */
    public List<IssueAction> issues() {
        return issues;
    }

    /**
     * Returns collection of reissues of assets after applying transaction.
     * @return collection of asset reissues or empty list
     */
    public List<ReissueAction> reissues() {
        return reissues;
    }

    /**
     * Returns collection of burns of assets after applying transaction.
     * @return collection of asset burns or empty list
     */
    public List<BurnAction> burns() {
        return burns;
    }

    /**
     * Returns collection of sponsorships of assets after applying transaction.
     * @return collection of asset sponsorships or empty list
     */
    public List<SponsorFeeAction> sponsorFees() {
        return sponsorFees;
    }

    /**
     * Returns error message if transaction is failed.
     * @return error message
     */
    public Error error() {
        return error;
    }

    public static class ScriptTransfer {

        private final Recipient recipient;
        private final long amount;
        private final AssetId assetId;

        @JsonCreator
        ScriptTransfer(@JsonProperty("address") Recipient recipient,
                       @JsonProperty("amount") long amount,
                       @JsonProperty("asset") AssetId assetId) {
            this.recipient = Common.notNull(recipient, "Recipient");
            this.amount = amount;
            this.assetId = assetId == null ? AssetId.WAVES : assetId;
        }

        public Recipient recipient() {
            return recipient;
        }

        public long amount() {
            return amount;
        }

        public AssetId assetId() {
            return assetId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScriptTransfer that = (ScriptTransfer) o;
            return amount == that.amount &&
                    Objects.equals(recipient, that.recipient) &&
                    Objects.equals(assetId, that.assetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(recipient, amount, assetId);
        }

        @Override
        public String toString() {
            return "ScriptTransfer{" +
                    "recipient=" + recipient +
                    ", amount=" + amount +
                    ", assetId=" + assetId +
                    '}';
        }
    }

    public static class IssueAction {
        private final String assetId;
        private final String name;
        private final String description;
        private final long quantity;
        private final int decimals;
        private final boolean reissuable;
        private final String compiledScript;
        private final int nonce;

        @JsonCreator
        IssueAction(@JsonProperty("assetId") String assetId,
                    @JsonProperty("name") String name,
                    @JsonProperty("description") String description,
                    @JsonProperty("quantity") long quantity,
                    @JsonProperty("decimals") int decimals,
                    @JsonProperty("isReissuable") boolean reissuable,
                    @JsonProperty("compiledScript") String compiledScript,
                    @JsonProperty("nonce") int nonce) {
            this.assetId = assetId;
            this.name = name;
            this.description = description;
            this.quantity = quantity;
            this.decimals = decimals;
            this.reissuable = reissuable;
            this.compiledScript = compiledScript;
            this.nonce = nonce;
        }

        public String assetId() {
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

        public String compiledScript() {
            return compiledScript;
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
                    Objects.equals(compiledScript, that.compiledScript);
        }

        @Override
        public int hashCode() {
            return Objects.hash(assetId, name, description, quantity, decimals, reissuable, compiledScript, nonce);
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
                    ", compiledScript='" + compiledScript + '\'' +
                    ", nonce=" + nonce +
                    '}';
        }
    }

    public static class ReissueAction {
        private final String assetId;
        private final long quantity;
        private final boolean reissuable;

        @JsonCreator
        ReissueAction(@JsonProperty("assetId") String assetId,
                    @JsonProperty("quantity") long quantity,
                    @JsonProperty("isReissuable") boolean reissuable) {
            this.assetId = assetId;
            this.quantity = quantity;
            this.reissuable = reissuable;
        }

        public String assetId() {
            return assetId;
        }

        public long quantity() {
            return quantity;
        }

        public boolean isReissuable() {
            return reissuable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReissueAction that = (ReissueAction) o;
            return quantity == that.quantity &&
                    reissuable == that.reissuable &&
                    Objects.equals(assetId, that.assetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(assetId, quantity, reissuable);
        }

        @Override
        public String toString() {
            return "ReissueAction{" +
                    "assetId='" + assetId + '\'' +
                    ", quantity=" + quantity +
                    ", reissuable=" + reissuable +
                    '}';
        }
    }

    public static class BurnAction {
        private final String assetId;
        private final long amount;

        @JsonCreator
        BurnAction(@JsonProperty("assetId") String assetId,
                    @JsonProperty("amount") long amount) {
            this.assetId = assetId;
            this.amount = amount;
        }

        public String assetId() {
            return assetId;
        }

        public long amount() {
            return amount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BurnAction that = (BurnAction) o;
            return amount == that.amount &&
                    Objects.equals(assetId, that.assetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(assetId, amount);
        }

        @Override
        public String toString() {
            return "BurnAction{" +
                    "assetId='" + assetId + '\'' +
                    ", amount=" + amount +
                    '}';
        }

    }

    public static class SponsorFeeAction {
        private final String assetId;
        private final long minSponsoredAssetFee;

        @JsonCreator
        SponsorFeeAction(@JsonProperty("assetId") String assetId,
                    @JsonProperty("minSponsoredAssetFee") long minSponsoredAssetFee) {
            this.assetId = assetId;
            this.minSponsoredAssetFee = minSponsoredAssetFee;
        }

        public String assetId() {
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

    public static class Error {
        private final int code;
        private final String text;

        @JsonCreator
        public Error(@JsonProperty("code") int code,
                     @JsonProperty("text") String text) {
            this.code = code;
            this.text = text;
        }

        public int code() {
            return code;
        }

        public String text() {
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Error error = (Error) o;
            return code == error.code &&
                    Objects.equals(text, error.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, text);
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
}
