package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putRecipient;

public class LeaseTransaction extends Transaction {
    static final byte LEASE = 8;
    private PublicKeyAccount sender;
    private String recipient;
    private long amount;
    private long fee;
    private long timestamp;

    public LeaseTransaction(PublicKeyAccount sender, String recipient, long amount, long fee, long timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
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
        buf.put(sender.getPublicKey());
        recipient = putRecipient(buf, sender.getChainId(), recipient);
        buf.putLong(amount).putLong(fee).putLong(timestamp);
        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", LEASE);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("recipient", recipient);
        data.put("amount", amount);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
