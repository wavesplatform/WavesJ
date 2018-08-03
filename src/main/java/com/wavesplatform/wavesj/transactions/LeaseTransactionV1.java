package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putRecipient;

public class LeaseTransactionV1 extends TransactionWithSignature implements LeaseTransaction {
    public static final byte LEASE = 8;

    private final PublicKeyAccount senderPublicKey;
    private final String recipient;
    private final long amount;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public LeaseTransactionV1(@JsonProperty("senderPublicKey") PrivateKeyAccount senderPublicKey,
                              @JsonProperty("recipient") String recipient,
                              @JsonProperty("amount") long amount,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.recipient = recipient;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @JsonCreator
    public LeaseTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("recipient") String recipient,
                              @JsonProperty("amount") long amount,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("signature") ByteString signature) {
        super(signature);
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
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return LEASE;
    }

    @Override
    public byte getVersion() {
        return Transaction.V2;
    }
}
