package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.StateChanges;

public class EthereumTransactionInfo extends TransactionInfo {

    private final StateChanges stateChanges;
    private final String bytes;

    public EthereumTransactionInfo(EthereumTransaction tx, ApplicationStatus applicationStatus, int height,
                                   StateChanges stateChanges, String bytes) {
        super(tx, applicationStatus, height);
        this.stateChanges = stateChanges;
        this.bytes = bytes;
    }

    public EthereumTransaction tx() {
        return (EthereumTransaction) super.tx();
    }

    public Boolean isTransferTransaction() {
        return stateChanges == null;
    }

    public Boolean isInvokeTransaction() {
        return stateChanges != null;
    }

    public StateChanges getStateChanges() {
        return stateChanges;
    }

    public String getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return "EthereumTransaction{} " + super.toString();
    }

}
