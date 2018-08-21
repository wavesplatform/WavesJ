package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class BurnTransactionV1 extends TransactionWithSignature implements BurnTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final String assetId;
    private final long amount;
    private final long fee;
    private final long timestamp;

    public BurnTransactionV1(PrivateKeyAccount senderPublicKey,
                             String assetId,
                             long amount,
                             long fee,
                             long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = new ByteString(senderPublicKey.sign(getBytes()));
    }

    @JsonCreator
    public BurnTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                             @JsonProperty("assetId") String assetId,
                             @JsonProperty("amount") long amount,
                             @JsonProperty("fee") long fee,
                             @JsonProperty("timestamp") long timestamp,
                             @JsonProperty("signature") ByteString signature) {
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
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
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(BurnTransaction.BURN)
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
        return Transaction.V1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BurnTransactionV1 that = (BurnTransactionV1) o;

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
        result = 31 * result + (getAssetId() != null ? getAssetId().hashCode() : 0);
        result = 31 * result + (int) (getAmount() ^ (getAmount() >>> 32));
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
