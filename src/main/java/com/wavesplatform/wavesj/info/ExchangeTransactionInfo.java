package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.ExchangeTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class ExchangeTransactionInfo extends TransactionInfo {

    public ExchangeTransactionInfo(ExchangeTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public ExchangeTransaction tx() {
        return (ExchangeTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "ExchangeTransactionInfo{} " + super.toString();
    }

}
