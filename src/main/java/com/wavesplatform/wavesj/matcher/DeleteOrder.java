package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class DeleteOrder extends ApiJson implements Signable {
    private PublicKeyAccount sender;
    private AssetPair assetPair;
    private String orderId;

    @JsonCreator
    public DeleteOrder(PublicKeyAccount sender, AssetPair assetPair, String orderId) {
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
    public byte[] getPublicKey() {
        return sender.getPublicKey();
    }
}
