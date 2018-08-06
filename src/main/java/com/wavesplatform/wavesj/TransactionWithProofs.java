package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TransactionWithProofs extends TransactionWithBytesHashId {
    public static final int MAX_PROOF_COUNT = 8;

    protected final List<ByteString> proofs;

    public TransactionWithProofs(List<ByteString> proofs) {
        if (proofs.size() >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("proofs count should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        this.proofs = Collections.unmodifiableList(proofs);
    }

    public TransactionWithProofs(PrivateKeyAccount account) {
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(account.sign(getBytes()))));
    }

    public List<ByteString> getProofs() {
        return proofs;
    }
}
