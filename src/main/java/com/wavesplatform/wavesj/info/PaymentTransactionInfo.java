package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.PaymentTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class PaymentTransactionInfo extends TransactionInfo {

    public PaymentTransactionInfo(PaymentTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public PaymentTransaction tx() {
        return (PaymentTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "PaymentTransactionInfo{} " + super.toString();
    }

}
