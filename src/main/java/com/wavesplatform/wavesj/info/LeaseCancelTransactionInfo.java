package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.LeaseCancelTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.LeaseInfo;

public class LeaseCancelTransactionInfo extends TransactionInfo {

    private final LeaseInfo leaseInfo;

    public LeaseCancelTransactionInfo(LeaseCancelTransaction tx, ApplicationStatus applicationStatus, int height, LeaseInfo leaseInfo) {
        super(tx, applicationStatus, height);
        this.leaseInfo = leaseInfo;
    }

    public LeaseCancelTransaction tx() {
        return (LeaseCancelTransaction) super.tx();
    }

    public LeaseInfo leaseInfo() {
        return leaseInfo;
    }

}
