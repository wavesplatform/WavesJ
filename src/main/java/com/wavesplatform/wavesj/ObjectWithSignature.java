package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.DataTransaction;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import com.wavesplatform.wavesj.transactions.SponsorTransaction;
import org.whispersystems.curve25519.Curve25519;

public class ObjectWithSignature<T extends Signable> extends ApiJson implements ProofedObject<T>, Signable {
    public static final byte V1 = 1;

    protected final ByteString signature;
    protected final T object;

    private static void checkAllowSignatureObject(Signable object) {
        if (object instanceof SponsorTransaction ||
                object instanceof SetScriptTransaction ||
                object instanceof MassTransferTransaction ||
                object instanceof DataTransaction) {
            throw new IllegalArgumentException("You need to use proofs for this transaction type");
        }
    }

    public ObjectWithSignature(T object, ByteString signature) {
        checkAllowSignatureObject(object);
        this.signature = signature;
        this.object = object;
    }

    public ObjectWithSignature(T object, PrivateKeyAccount account) {
        checkAllowSignatureObject(object);
        this.object = object;
        this.signature = new ByteString(account.sign(getBytes()));
    }

    public ByteString getSignature() {
        return signature;
    }

    public T getObject() {
        return object;
    }

    public byte getVersion() {
        return V1;
    }

    @Override
    public byte[] getBytes() {
        byte[] bytePrefix = new byte[]{};
        if (object instanceof Transaction) {
            bytePrefix = new byte[]{((Transaction) object).getType()};
        }
        return ByteArraysUtils.addAll(bytePrefix, object.getBytes());
    }

    @Override
    public byte[] getPublicKey() {
        return object.getPublicKey();
    }

    public boolean verifySignature() {
        return Curve25519.getInstance(Curve25519.BEST).verifySignature(getPublicKey(), getBytes(), signature.getBytes());
    }
}
