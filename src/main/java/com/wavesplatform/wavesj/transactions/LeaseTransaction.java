package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;

public interface LeaseTransaction extends Transaction, Signable, WithId {
    static final byte LEASE = 8;

    String getRecipient();

    long getAmount();

    long getFee();

    long getTimestamp();
}
