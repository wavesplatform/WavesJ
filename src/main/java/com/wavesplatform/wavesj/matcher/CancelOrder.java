package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class CancelOrder extends ObjectWithSignature implements ApiJson {
    private final PublicKeyAccount sender;
    private final AssetPair assetPair;
    private final String orderId;

    @JsonCreator
    public CancelOrder(PrivateKeyAccount sender, AssetPair assetPair, String orderId) {
        super(sender);
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = orderId;
    }

    @JsonCreator
    public CancelOrder(PublicKeyAccount sender, AssetPair assetPair, String orderId, ByteString signature) {
        super(signature);
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = orderId;
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

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(sender.getPublicKey()).put(Base58.decode(orderId));
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return sender;
    }
}
