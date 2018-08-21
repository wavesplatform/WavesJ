package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;

public interface BurnTransaction extends Transaction, Signable, WithId {
    static final byte BURN = 6;

    String getAssetId();

    long getAmount();
}
