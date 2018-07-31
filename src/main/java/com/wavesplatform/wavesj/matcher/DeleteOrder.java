package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class DeleteOrder extends ApiJson implements Signable {
    private PublicKeyAccount sender;
    private AssetPair assetPair;
    private String orderId;

    public DeleteOrder(PublicKeyAccount sender, AssetPair assetPair, String orderId) {
        this.sender = sender;
        this.assetPair = assetPair;
        this.orderId = orderId;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public AssetPair getAssetPair() {
        return assetPair;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("sender", Base58.encode(sender.getPublicKey()));
        data.put("orderId", orderId);
        return data;
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
}
