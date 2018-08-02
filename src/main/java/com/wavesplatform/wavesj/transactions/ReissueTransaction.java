package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class ReissueTransaction extends Transaction {
    public static final byte REISSUE = 5;

    private PublicKeyAccount sender;
    private byte chainId;
    private String assetId;
    private long quantity;
    private boolean reissuable;
    private long fee;
    private long timestamp;

    @JsonCreator
    public ReissueTransaction(@JsonProperty("sender") PublicKeyAccount sender,
                              @JsonProperty("chainId") byte chainId,
                              @JsonProperty("assetId") String assetId,
                              @JsonProperty("quantity") long quantity,
                              @JsonProperty("reissuable") boolean reissuable,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp) {
        this.sender = sender;
        this.chainId = chainId;
        this.assetId = assetId;
        this.quantity = quantity;
        this.reissuable = reissuable;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
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
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(sender.getPublicKey()).put(Base58.decode(assetId)).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return REISSUE;
    }
}
