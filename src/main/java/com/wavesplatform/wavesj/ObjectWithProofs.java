package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.DataTransaction;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import com.wavesplatform.wavesj.transactions.SponsorTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectWithProofs<T extends Transaction> extends ApiJson implements ProofedObject<T>, Proofable {
    public static final int MAX_PROOF_COUNT = 8;
    public static final byte V1 = 1;
    public static final byte V2 = 2;

    protected final List<ByteString> proofs;
    protected final T object;

    public ObjectWithProofs(T object, List<ByteString> proofs) {
        if (proofs.size() >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("proofs count should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        this.object = object;
        this.proofs = proofs;
    }

    public ObjectWithProofs(Proofable object, PrivateKeyAccount account) {
        this.object = (T) object;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(account.sign(getBytes()))));
    }

    public T getObject() {
        return object;
    }

    public List<ByteString> getProofs() {
        return proofs;
    }

    public byte getVersion() {
        byte version = V2;
        if (object instanceof SponsorTransaction ||
                object instanceof SetScriptTransaction ||
                object instanceof MassTransferTransaction ||
                object instanceof DataTransaction) {
            version = V1;
        }
        return version;
    }

    /**
     * Returns a new {@code Transaction} object with the proof added.
     *
     * @param proof a Base58-encoded proof
     * @return new {@code Transaction} object with the proof added
     * @throws IllegalArgumentException if index is not between 0 and 7
     */
    public ObjectWithProofs<T> withProof(int index, ByteString proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<ByteString> newProofs = new ArrayList<ByteString>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add(ByteString.EMPTY);
        }
        newProofs.set(index, proof);
        return new ObjectWithProofs<T>(object, Collections.unmodifiableList(newProofs));
    }

    public byte[] getBytes() {
        return ByteArraysUtils.addAll(new byte[]{object.getType(), getVersion()}, object.getBytes());
    }
}
