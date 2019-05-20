package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class ReissueTransactionV2 extends TransactionWithProofs<ReissueTransactionV2> implements ReissueTransaction {
    public static final byte REISSUE = 5;

    private PublicKeyAccount senderPublicKey;
    private byte chainId;
    private String assetId;
    private long quantity;
    private boolean reissuable;
    private long fee;
    private long timestamp;
    private static final int MAX_TX_SIZE = KBYTE;

    @JsonCreator
    public ReissueTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                @JsonProperty("chainId") byte chainId,
                                @JsonProperty("address") String assetId,
                                @JsonProperty("quantity") long quantity,
                                @JsonProperty("reissuable") boolean reissuable,
                                @JsonProperty("fee") long fee,
                                @JsonProperty("timestamp") long timestamp,
                                @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.quantity = quantity;
        this.reissuable = reissuable;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public ReissueTransactionV2(PrivateKeyAccount senderPublicKey,
                                byte chainId,
                                String assetId,
                                long quantity,
                                boolean reissuable,
                                long fee,
                                long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.quantity = quantity;
        this.reissuable = reissuable;
        this.fee = fee;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
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

    public long getQuantity() {
        return quantity;
    }

    public boolean isReissuable() {
        return reissuable;
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
        ByteBuffer buf = ByteBuffer.allocate(MAX_TX_SIZE);
        buf.put(ReissueTransaction.REISSUE).put(Transaction.V2).put(chainId)
                .put(senderPublicKey.getPublicKey()).put(Base58.decode(assetId)).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return REISSUE;
    }

    @Override
    public byte getVersion() {
        return Transaction.V2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReissueTransactionV2 that = (ReissueTransactionV2) o;

        if (getChainId() != that.getChainId()) return false;
        if (getQuantity() != that.getQuantity()) return false;
        if (isReissuable() != that.isReissuable()) return false;
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
        result = 31 * result + (int) (getQuantity() ^ (getQuantity() >>> 32));
        result = 31 * result + (isReissuable() ? 1 : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }

    @Override
    public ReissueTransactionV2 withProof(int index, ByteString proof) {
        List<ByteString> newProofs = updateProofs(index, proof);
        return new ReissueTransactionV2(senderPublicKey, chainId, assetId, quantity, reissuable, fee, timestamp, newProofs);
    }
}
