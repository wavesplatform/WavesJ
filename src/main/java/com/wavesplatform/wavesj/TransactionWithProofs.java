package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.TransactionWithBytesHashId;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public abstract class TransactionWithProofs<T extends Transaction> extends TransactionWithBytesHashId {
    public static final int MAX_PROOF_COUNT = 8;

    protected List<ByteString> proofs;

    protected void setProofs(List<ByteString> proofs) {
        if (proofs.size() >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("proofs count should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        this.proofs = Collections.unmodifiableList(proofs);
    }

    public abstract T withProof(int index, ByteString proof);

    public List<ByteString> getProofs() {
        return proofs;
    }

    protected List<ByteString> updateProofs(int index, ByteString proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<ByteString> newProofs = new ArrayList<ByteString>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add(ByteString.EMPTY);
        }
        newProofs.set(index, proof);
        return newProofs;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(getBodyBytes())
                .put((byte) 1) //proofs version
                .putShort((short) getProofs().size());
        getProofs().forEach(p -> buf
                .putShort((short) p.getBytes().length)
                .put(p.getBytes()));
        return ByteArraysUtils.getOnlyUsed(buf);
    }
}
