package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class BurnTransactionV2 extends TransactionWithProofs<BurnTransactionV2> implements BurnTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String assetId;
    private final long amount;
    private final long fee;
    private final long timestamp;
    private static final int MAX_TX_SIZE = KBYTE;

    public BurnTransactionV2(PrivateKeyAccount senderPublicKey,
                             byte chainId,
                             String assetId,
                             long amount,
                             long fee,
                             long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
    }

    @JsonCreator
    public BurnTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                             @JsonProperty("chainId") byte chainId,
                             @JsonProperty("assetId") String assetId,
                             @JsonProperty("amount") long amount,
                             @JsonProperty("fee") long fee,
                             @JsonProperty("timestamp") long timestamp,
                             @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte getChainId() {
        return chainId;
    }

    public String getAssetId() {
        return assetId;
    }

    public long getAmount() {
        return amount;
    }

    public long getFee() {
        return fee;
    }

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
        buf.put(BurnTransaction.BURN).put(Transaction.V2).put(chainId)
                .put(senderPublicKey.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return BURN;
    }

    @Override
    public byte getVersion() {
        return Transaction.V2;
    }

    @Override
    public BurnTransactionV2 withProof(int index, ByteString proof) {
        List<ByteString> newProofs = updateProofs(index, proof);
        return new BurnTransactionV2(senderPublicKey, chainId, assetId, amount, fee, timestamp, newProofs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BurnTransactionV2 that = (BurnTransactionV2) o;

        if (getChainId() != that.getChainId()) return false;
        if (getAmount() != that.getAmount()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        return getAssetId() != null ? getAssetId().equals(that.getAssetId()) : that.getAssetId() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (int) getChainId();
        result = 31 * result + (getAssetId() != null ? getAssetId().hashCode() : 0);
        result = 31 * result + (int) (getAmount() ^ (getAmount() >>> 32));
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
