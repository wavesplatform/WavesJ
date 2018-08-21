package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.TransactionWithBytesHashId;

import java.util.Collections;
import java.util.List;

public abstract class TransactionWithProofs extends TransactionWithBytesHashId {
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
}
