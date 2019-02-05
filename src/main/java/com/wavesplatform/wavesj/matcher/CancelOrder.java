package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class CancelOrder extends ObjectWithSignature implements ApiJson {
    private final PublicKeyAccount sender;
    private final AssetPair assetPair;
    private final String orderId;
    private final Long timestamp;

    public CancelOrder(PrivateKeyAccount sender, AssetPair assetPair, String orderId) {
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = orderId;
        this.timestamp = null;
        this.signature = new ByteString(sender.sign(getBodyBytes()));
    }

    public CancelOrder(PrivateKeyAccount sender, long timestamp) {
        this.sender = sender;
        this.assetPair = null;
        this.orderId = null;
        this.timestamp = timestamp;
        this.signature = new ByteString(sender.sign(getBodyBytes()));
    }

    public CancelOrder(PrivateKeyAccount sender, AssetPair assetPair, long timestamp) {
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = null;
        this.timestamp = timestamp;
        this.signature = new ByteString(sender.sign(getBodyBytes()));
    }

    public CancelOrder(PublicKeyAccount sender, AssetPair assetPair, String orderId, ByteString signature) {
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = orderId;
        this.timestamp = null;
        this.signature = signature;
    }

    public CancelOrder(PublicKeyAccount sender, AssetPair assetPair, long timestamp, ByteString signature) {
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = null;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    @JsonIgnore
    public AssetPair getAssetPair() {
        return assetPair;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(sender.getPublicKey());
        if (orderId != null) {
            buf.put(Base58.decode(orderId));
        } else {
            buf.putLong(timestamp);
        }
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(getBodyBytes())
                .put(signature.getBytes());
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return sender;
    }
}
