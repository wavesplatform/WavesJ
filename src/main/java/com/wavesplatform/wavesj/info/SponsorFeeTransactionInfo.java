package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.SponsorFeeTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class SponsorFeeTransactionInfo extends TransactionInfo {

    public SponsorFeeTransactionInfo(SponsorFeeTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public SponsorFeeTransaction tx() {
        return (SponsorFeeTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "SponsorFeeTransactionInfo{} " + super.toString();
    }

}
