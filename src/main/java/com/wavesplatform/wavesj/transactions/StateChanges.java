package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.Transfer;

import java.util.Collection;
import java.util.Collections;

public class StateChanges {
    private Collection<DataEntry<?>> data;
    private Collection<OutTransfer> transfers;
    private Collection<IssueAction> issues;
    private Collection<ReissueAction> reissues;
    private Collection<BurnAction> burns;
    private Collection<SponsorFeeAction> sponsorFees;
    private Error error;

    @JsonCreator
    StateChanges(@JsonProperty("data") Collection<DataEntry<?>> data,
                 @JsonProperty("transfers") Collection<OutTransfer> transfers,
                 @JsonProperty("issues") Collection<IssueAction> issues,
                 @JsonProperty("reissues") Collection<ReissueAction> reissues,
                 @JsonProperty("burns") Collection<BurnAction> burns,
                 @JsonProperty("sponsorFees") Collection<SponsorFeeAction> sponsorFees,
                 @JsonProperty(value = "error", required = false) Error error) {
        this.data = data != null ? data : Collections.<DataEntry<?>>emptyList();
        this.transfers = transfers != null ? transfers : Collections.<OutTransfer>emptyList();
        this.issues = issues != null ? issues : Collections.<IssueAction>emptyList();
        this.reissues = reissues != null ? reissues : Collections.<ReissueAction>emptyList();
        this.burns = burns != null ? burns : Collections.<BurnAction>emptyList();
        this.sponsorFees = sponsorFees != null ? sponsorFees : Collections.<SponsorFeeAction>emptyList();
        this.error = error != null ? error : new Error(0, "");
    }

    /**
     * Returns collection of account's {@link DataEntry DataEntries} which were modified after applying transaction
     * @return collection of {@link DataEntry DataEntries} or empty list
     */
    public Collection<DataEntry<?>> getData() {
        return data;
    }

    /**
     * Returns collection of output transfers which were triggered after applying transaction. Currently such transfers
     * are supported by ScriptInvocationTransaction only
     * @return collection of transfers or empty list
     */
    public Collection<OutTransfer> getTransfers() {
        return transfers;
    }

    /**
     * Returns collection of assets which were issued after applying transaction.
     * @return collection of issued assets or empty list
     */
    public Collection<IssueAction> getIssues() {
        return issues;
    }

    /**
     * Returns collection of reissues of assets after applying transaction.
     * @return collection of asset reissues or empty list
     */
    public Collection<ReissueAction> getReissues() {
        return reissues;
    }

    /**
     * Returns collection of burns of assets after applying transaction.
     * @return collection of asset burns or empty list
     */
    public Collection<BurnAction> getBurns() {
        return burns;
    }

    /**
     * Returns collection of sponsorships of assets after applying transaction.
     * @return collection of asset sponsorships or empty list
     */
    public Collection<SponsorFeeAction> getSponsorFees() {
        return sponsorFees;
    }

    /**
     * Returns error message if transaction is failed.
     * @return error message
     */
    public Error getError() {
        return error;
    }

    public static class OutTransfer extends Transfer {

        private String assetId;

        @JsonCreator
        OutTransfer(@JsonProperty("address") String recipient,
                    @JsonProperty("amount") long amount,
                    @JsonProperty("asset") String assetId) {
            super(recipient, amount);
            this.assetId = assetId;
        }

        public String getAssetId() {
            return assetId;
        }
    }

    public static class IssueAction {
        private String assetId;
        private String name;
        private String description;
        private long quantity;
        private int decimals;
        private boolean reissuable;
        private String compiledScript;
        private int nonce;

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

        public String getAssetId() {
            return assetId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public long getQuantity() {
            return quantity;
        }

        public int getDecimals() {
            return decimals;
        }

        public boolean isReissuable() {
            return reissuable;
        }

        public String getCompiledScript() {
            return compiledScript;
        }

        public int getNonce() {
            return nonce;
        }
    }

    public static class ReissueAction {
        private String assetId;
        private long quantity;
        private boolean reissuable;

        @JsonCreator
        ReissueAction(@JsonProperty("assetId") String assetId,
                    @JsonProperty("quantity") long quantity,
                    @JsonProperty("isReissuable") boolean reissuable) {
            this.assetId = assetId;
            this.quantity = quantity;
            this.reissuable = reissuable;
        }

        public String getAssetId() {
            return assetId;
        }

        public long getQuantity() {
            return quantity;
        }

        public boolean isReissuable() {
            return reissuable;
        }
    }

    public static class BurnAction {
        private String assetId;
        private long amount;

        @JsonCreator
        BurnAction(@JsonProperty("assetId") String assetId,
                    @JsonProperty("amount") long amount) {
            this.assetId = assetId;
            this.amount = amount;
        }

        public String getAssetId() {
            return assetId;
        }

        public long getAmount() {
            return amount;
        }
    }

    public static class SponsorFeeAction {
        private String assetId;
        private long minSponsoredAssetFee;

        @JsonCreator
        SponsorFeeAction(@JsonProperty("assetId") String assetId,
                    @JsonProperty("minSponsoredAssetFee") long minSponsoredAssetFee) {
            this.assetId = assetId;
            this.minSponsoredAssetFee = minSponsoredAssetFee;
        }

        public String getAssetId() {
            return assetId;
        }

        public long getMinSponsoredAssetFee() {
            return minSponsoredAssetFee;
        }
    }

    public static class Error {
        private int code;
        private String text;

        @JsonCreator
        public Error(@JsonProperty("code") int code,
                     @JsonProperty("text") String text) {
            this.code = code;
            this.text = text;
        }

        public int getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }
}
