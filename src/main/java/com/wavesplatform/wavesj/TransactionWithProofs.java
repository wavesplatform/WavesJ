package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.TransactionWithBytesHashId;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public abstract class TransactionWithProofs<T extends Transaction> extends TransactionWithBytesHashId {
    private static final int MAX_PROOF_COUNT = 8;
    private static final int MAX_TX_SIZE = 10 * KBYTE;

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

//    public String getIdStr(){
//        return  getId().toString();
//    }

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

    protected int getTransactionMaxSize() {
        return MAX_TX_SIZE;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(getTransactionMaxSize());
        buf.put(getBodyBytes())
                .put((byte) 1) //proofs version
                .putShort((short) getProofs().size());
        for (ByteString p : getProofs()) {
            buf
                    .putShort((short) p.getBytes().length)
                    .put(p.getBytes());
        }
        return ByteArraysUtils.getOnlyUsed(buf);
    }
}
