package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.StateChanges;

public class EthereumTransactionInfo extends TransactionInfo {
    private StateChanges stateChanges;

    public EthereumTransactionInfo(EthereumTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    @Override
    public EthereumTransaction tx() {
        return (EthereumTransaction) super.tx();
    }
}
