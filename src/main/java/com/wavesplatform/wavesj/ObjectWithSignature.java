package com.wavesplatform.wavesj;

import org.whispersystems.curve25519.Curve25519;

public abstract class ObjectWithSignature implements Signable {
    protected final ByteString signature;

    public ObjectWithSignature(ByteString signature) {
        this.signature = signature;
    }

    public ObjectWithSignature(PrivateKeyAccount account) {
        this.signature = new ByteString(account.sign(getBytes()));
    }

    public ByteString getSignature() {
        return signature;
    }

    public boolean verifySignature() {
        return Curve25519.getInstance(Curve25519.BEST).verifySignature(getSenderPublicKey().getPublicKey(), getBytes(), signature.getBytes());
    }
}
