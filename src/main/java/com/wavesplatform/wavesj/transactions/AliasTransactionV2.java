package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class AliasTransactionV2 extends TransactionWithProofs<AliasTransactionV2> implements AliasTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final Alias alias;
    private final long fee;
    private final long timestamp;
    private static final int MAX_TX_SIZE = KBYTE;

    @Override
    public ByteString getId() {
        return new ByteString(hash(ByteArraysUtils.addAll(new byte[]{ALIAS}, alias.getBytes())));
    }

    public AliasTransactionV2(PrivateKeyAccount senderPublicKey,
                              Alias alias,
                              long fee,
                              long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.alias = alias;
        this.fee = fee;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
    }

    @JsonCreator
    public AliasTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("alias") Alias alias,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("proofs") List<ByteString> proofs
    ) {
        setProofs(proofs);
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
    public int getTransactionMaxSize(){
        return MAX_TX_SIZE;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(getTransactionMaxSize());
        buf.put(ALIAS).put(Transaction.V2).put(senderPublicKey.getPublicKey());
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
        List<ByteString> newProofs = updateProofs(index, proof);
        return new AliasTransactionV2(senderPublicKey, alias, fee, timestamp, newProofs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AliasTransactionV2 that = (AliasTransactionV2) o;

        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        return getAlias() != null ? getAlias().equals(that.getAlias()) : that.getAlias() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getAlias() != null ? getAlias().hashCode() : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
