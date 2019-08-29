package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.Transfer;

import java.util.Collection;

public class StateChanges {
    private Collection<DataEntry<?>> data;
    private Collection<OutTransfer> transfers;

    @JsonCreator
    StateChanges(@JsonProperty("data") Collection<DataEntry<?>> data,
                 @JsonProperty("transfers") Collection<OutTransfer> transfers) {
        this.data = data;
        this.transfers = transfers;
    }

    public Collection<DataEntry<?>> getData() {
        return data;
    }

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
