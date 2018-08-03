package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wavesplatform.wavesj.*;
import org.whispersystems.curve25519.Curve25519;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class DeleteOrder extends ObjectWithSignature implements ApiJson {
    private PublicKeyAccount sender;
    private AssetPair assetPair;
    private String orderId;

    public DeleteOrder(PrivateKeyAccount sender, AssetPair assetPair, String orderId) {
        super(sender);
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = orderId;
    }

    @JsonCreator
    public DeleteOrder(PublicKeyAccount sender, AssetPair assetPair, String orderId, ByteString signature) {
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
