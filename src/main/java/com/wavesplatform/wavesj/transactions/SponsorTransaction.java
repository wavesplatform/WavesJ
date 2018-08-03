package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class SponsorTransaction extends TransactionWithBytesHashId {
    public static final byte SPONSOR = 14;
    private PublicKeyAccount senderPublicKey;
    private String assetId;
    private long minAssetFee;
    private long fee;
    private long timestamp;

    @JsonCreator
    public SponsorTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("assetId") String assetId,
                              @JsonProperty("minAssetFee") long minAssetFee,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.minAssetFee = minAssetFee;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public String getAssetId() {
        return Asset.toJsonObject(assetId);
    }

    public long getMinAssetFee() {
        return minAssetFee;
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
        buf.put(senderPublicKey.getPublicKey()).put(Base58.decode(assetId))
                .putLong(minAssetFee).putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return SPONSOR;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }
}
