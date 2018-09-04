package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;

public interface ReissueTransaction extends Transaction, Signable, WithId {
    public static final byte REISSUE = 5;

    String getAssetId();

    long getQuantity();

    boolean isReissuable();
}
