package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.MassTransferTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class MassTransferTransactionInfo extends TransactionInfo {

    public MassTransferTransactionInfo(MassTransferTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public MassTransferTransaction tx() {
        return (MassTransferTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "MassTransferTransactionInfo{} " + super.toString();
    }

}
