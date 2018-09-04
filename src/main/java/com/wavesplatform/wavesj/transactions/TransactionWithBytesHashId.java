package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.Transaction;

import static com.wavesplatform.wavesj.ByteUtils.hash;

public abstract class TransactionWithBytesHashId implements Transaction {
    public ByteString getId() {
        return new ByteString(hash(getBytes()));
    }
}
