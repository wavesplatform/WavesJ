package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class BurnTransaction extends Transaction {
    public static final byte BURN = 6;

    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String assetId;
    private final long amount;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public BurnTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                           @JsonProperty("chainId") byte chainId,
                           @JsonProperty("assetId") String assetId,
                           @JsonProperty("amount") long amount,
                           @JsonProperty("fee") long fee,
                           @JsonProperty("timestamp") long timestamp) {
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
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(senderPublicKey.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return BURN;
    }
}
