package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.ApiJson;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.util.*;

public class TransactionWithProofs<T extends Transaction> extends ApiJson {

    public static final int MAX_PROOF_COUNT = 8;
    static final byte V2 = 2;

    protected final List<String> proofs;
    protected final T transaction;

    public TransactionWithProofs(T transaction, List<String> proofs) {
        if (proofs.size() >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("proofs count should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        this.transaction = transaction;
        this.proofs = proofs;
    }

    public TransactionWithProofs(T transaction, PrivateKeyAccount account) {
        this(transaction, Collections.singletonList(account.sign(transaction)));
    }

    public List<String> getProofs() {
        return proofs;
    }

    /**
     * Returns a new {@code Transaction} object with the proof added.
     *
     * @param proof a Base58-encoded proof
     * @return new {@code Transaction} object with the proof added
     * @throws IllegalArgumentException if index is not between 0 and 7
     */
    public void setProof(int index, String proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        for (int i = proofs.size(); i <= index; i++) {
            proofs.add("");
        }
        proofs.set(index, proof);
    }

    public Map<String, Object> getData() {
        HashMap<String, Object> toJson = new HashMap<String, Object>(getData());
        toJson.put("id", transaction.getId());
        toJson.put("version", V2);
        toJson.put("proofs", proofs);
        return toJson;

    }
}
