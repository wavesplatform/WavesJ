package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class AliasTransactionV2 extends TransactionWithProofs implements AliasTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final Alias alias;
    private final long fee;
    private final long timestamp;

    @Override
    public ByteString getId() {
        return new ByteString(hash(ByteArraysUtils.addAll(new byte[]{ALIAS}, alias.getBytes())));
    }

    @JsonCreator
    public AliasTransactionV2(@JsonProperty("senderPublicKey") PrivateKeyAccount senderPublicKey,
                              @JsonProperty("alias") Alias alias,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.alias = alias;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @JsonCreator
    public AliasTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("alias") Alias alias,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("proofs") List<ByteString> proofs
    ) {
        super(proofs);
        this.senderPublicKey = senderPublicKey;
        this.alias = alias;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public Alias getAlias() {
        return alias;
    }

    @Override
    public long getFee() {
        return fee;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(ALIAS).put(getVersion()).put(senderPublicKey.getPublicKey());
        putBytes(buf, alias.getBytes());
        buf.putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getVersion() {
        return Transaction.V2;
    }

    @Override
    public byte getType() {
        return ALIAS;
    }

    public AliasTransactionV2 withProof(int index, ByteString proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<ByteString> newProofs = new ArrayList<ByteString>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add(ByteString.EMPTY);
        }
        newProofs.set(index, proof);
        return new AliasTransactionV2(senderPublicKey, alias, fee, timestamp, Collections.unmodifiableList(newProofs));
    }
}
