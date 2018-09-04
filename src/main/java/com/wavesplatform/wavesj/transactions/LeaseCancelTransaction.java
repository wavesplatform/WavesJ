package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;

public interface LeaseCancelTransaction extends Transaction, Signable, WithId {
    public static final byte LEASE_CANCEL = 9;

    public String getLeaseId();
}
