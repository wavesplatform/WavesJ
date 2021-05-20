package com.wavesplatform.wavesj.actions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Recipient;
import com.wavesplatform.wavesj.LeaseStatus;

public class LeaseCancelInfo extends LeaseInfo {

    private final int cancelHeight;
    private final Id cancelTransactionId;

    public LeaseCancelInfo(Id id, Id originTransactionId, Address sender, Recipient recipient, long amount, int height, LeaseStatus status, int cancelHeight, Id cancelTransactionId) {
        super(id, originTransactionId, sender, recipient, amount, height, status);
        this.cancelHeight = cancelHeight;
        this.cancelTransactionId = cancelTransactionId;
    }

    public int cancelHeight() {
        return cancelHeight;
    }

    public Id cancelTransactionId() {
        return cancelTransactionId;
    }

}
