package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.BurnTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class BurnTransactionInfo extends TransactionInfo {

    public BurnTransactionInfo(BurnTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public BurnTransaction tx() {
        return (BurnTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "BurnTransactionInfo{} " + super.toString();
    }

}
