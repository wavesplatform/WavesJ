package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class BurnTransaction extends Transaction {
    static final byte BURN = 6;

    private PublicKeyAccount sender;
    private byte chainId;
    private String assetId;
    private long amount;
    private long fee;
    private long timestamp;

    public BurnTransaction(PublicKeyAccount sender, byte chainId, String assetId, long amount, long fee, long timestamp) {
        this.sender = sender;
        this.chainId = chainId;
        this.assetId = assetId;
        this.amount = amount;
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
        buf.put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", BURN);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("assetId", assetId);
        data.put("quantity", amount);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
