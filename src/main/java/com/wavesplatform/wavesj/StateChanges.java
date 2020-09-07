package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.actions.*;
import com.wavesplatform.wavesj.actions.Error;
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

}
