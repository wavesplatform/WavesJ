package com.wavesplatform.wavesj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectWithProofs<T extends Proofable> extends ApiJson implements ProofedObject<T> {
    public static final int MAX_PROOF_COUNT = 8;
    public static final byte V2 = 2;

    protected final List<String> proofs;
    protected final T object;

    public ObjectWithProofs(T object, List<String> proofs) {
        if (proofs.size() >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("proofs count should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        this.object = object;
        this.proofs = proofs;
    }

    public ObjectWithProofs(Proofable object, PrivateKeyAccount account) {
        this((T) object, Collections.singletonList(account.sign(object.getBytes())));
    }

    public T getObject() {
        return object;
    }

    public List<String> getProofs() {
        return proofs;
    }

    public byte getVersion() {
        return V2;
    }

    /**
     * Returns a new {@code Transaction} object with the proof added.
     *
     * @param proof a Base58-encoded proof
     * @return new {@code Transaction} object with the proof added
     * @throws IllegalArgumentException if index is not between 0 and 7
     */
    public ObjectWithProofs<T> withProof(int index, String proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<String> newProofs = new ArrayList<String>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add("");
        }
        newProofs.set(index, proof);
        return new ObjectWithProofs<T>(object, newProofs);
    }
}
