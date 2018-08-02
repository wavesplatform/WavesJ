package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putRecipient;

public class LeaseTransaction extends Transaction {
    public static final byte LEASE = 8;

    private final PublicKeyAccount senderPublicKey;
    private final String recipient;
    private final long amount;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public LeaseTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                            @JsonProperty("recipient") String recipient,
                            @JsonProperty("amount") long amount,
                            @JsonProperty("fee") long fee,
                            @JsonProperty("timestamp") long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.recipient = recipient;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
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
        buf.put(senderPublicKey.getPublicKey());
        putRecipient(buf, senderPublicKey.getChainId(), recipient);
        buf.putLong(amount).putLong(fee).putLong(timestamp);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return LEASE;
    }
}
