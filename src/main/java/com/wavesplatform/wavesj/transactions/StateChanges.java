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

    @JsonCreator
    StateChanges(@JsonProperty("data") Collection<DataEntry<?>> data,
                 @JsonProperty("transfers") Collection<OutTransfer> transfers) {
        this.data = data != null ? data : Collections.<DataEntry<?>>emptyList();
        this.transfers = transfers != null ? transfers : Collections.<OutTransfer>emptyList();
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
}
