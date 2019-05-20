package com.wavesplatform.wavesj;

import org.whispersystems.curve25519.Curve25519;

import java.util.Collections;
import java.util.List;

public abstract class ObjectWithProofs implements Signable {
    public static final int MAX_PROOF_COUNT = 8;

    protected List<ByteString> proofs;

    protected void setProofs(List<ByteString> proofs) {
        if (proofs.size() >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("proofs count should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        this.proofs = Collections.unmodifiableList(proofs);
    }

    public List<ByteString> getProofs() {
        return proofs;
    }

    public boolean verifySignature() {
        return Curve25519.getInstance(Curve25519.BEST).verifySignature(getSenderPublicKey().getPublicKey(), getBodyBytes(), getProofs().get(0).getBytes());
    }
}
