package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class DataTransactionInfo extends TransactionInfo {

    public DataTransactionInfo(DataTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public DataTransaction tx() {
        return (DataTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "DataTransactionInfo{} " + super.toString();
    }

}
