package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class SponsorTransaction extends TransactionWithProofs {
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
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("proofs") List<ByteString> proofs) {
        super(proofs);
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.minAssetFee = minAssetFee;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public SponsorTransaction(PrivateKeyAccount senderPublicKey,
                                String assetId,
                                long minAssetFee,
                                long fee,
                                long timestamp) {
        super(senderPublicKey);
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
        buf.put(SponsorTransaction.SPONSOR).put(Transaction.V1)
                .put(senderPublicKey.getPublicKey()).put(Base58.decode(assetId))
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
