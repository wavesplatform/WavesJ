package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.Transaction;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static com.wavesplatform.wavesj.ByteUtils.hash;

public abstract class TransactionWithBytesHashId implements Transaction {
    @JsonProperty(value = "height", access = WRITE_ONLY)
    private int height;

    public ByteString getId() {
        return new ByteString(hash(getBytes()));
    }

    public int getHeight() {
        return height;
    }
}
