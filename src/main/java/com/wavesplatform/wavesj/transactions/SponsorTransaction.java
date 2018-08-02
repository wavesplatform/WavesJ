package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class SponsorTransaction extends Transaction {
    public static final byte SPONSOR = 14;
    private PublicKeyAccount sender;
    private String assetId;
    private long minAssetFee;
    private long fee;
    private long timestamp;

    @JsonCreator
    public SponsorTransaction(@JsonProperty("sender") PublicKeyAccount sender,
                              @JsonProperty("assetId") String assetId,
                              @JsonProperty("minAssetFee") long minAssetFee,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp) {
        this.sender = sender;
        this.assetId = assetId;
        this.minAssetFee = minAssetFee;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
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
        buf.put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(minAssetFee).putLong(fee).putLong(timestamp);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return SPONSOR;
    }
}
