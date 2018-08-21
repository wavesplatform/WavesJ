package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.TransactionWithBytesHashId;
import org.whispersystems.curve25519.Curve25519;

public abstract class TransactionWithSignature extends TransactionWithBytesHashId {
    protected ByteString signature;

    public ByteString getSignature() {
        return signature;
    }

    public boolean verifySignature() {
        return Curve25519.getInstance(Curve25519.BEST).verifySignature(getSenderPublicKey().getPublicKey(), getBytes(), signature.getBytes());
    }
}
