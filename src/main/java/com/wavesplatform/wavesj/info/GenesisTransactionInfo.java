package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.GenesisTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class GenesisTransactionInfo extends TransactionInfo {

    public GenesisTransactionInfo(GenesisTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public GenesisTransaction tx() {
        return (GenesisTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "GenesisTransactionInfo{} " + super.toString();
    }

}
