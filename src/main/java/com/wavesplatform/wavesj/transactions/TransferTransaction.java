package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;

public interface TransferTransaction extends Transaction, Signable, WithId {
    static final byte TRANSFER = 4;

    String getRecipient();

    long getAmount();

    String getAssetId();

    String getFeeAssetId();

    ByteString getAttachment();
}
