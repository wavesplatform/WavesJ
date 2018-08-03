package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putRecipient;

public class LeaseTransactionV2 extends TransactionWithProofs implements LeaseTransaction {
    public static final byte LEASE = 8;

    private final PublicKeyAccount senderPublicKey;
    private final String recipient;
    private final long amount;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public LeaseTransactionV2(@JsonProperty("senderPublicKey") PrivateKeyAccount senderPublicKey,
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
    public LeaseTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                            @JsonProperty("recipient") String recipient,
                            @JsonProperty("amount") long amount,
                            @JsonProperty("fee") long fee,
                            @JsonProperty("timestamp") long timestamp,
                            @JsonProperty("proofs") List<ByteString> proofs) {
        super(proofs);
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

    public LeaseTransactionV2 withProof(int index, ByteString proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<ByteString> newProofs = new ArrayList<ByteString>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add(ByteString.EMPTY);
        }
        newProofs.set(index, proof);
        return new LeaseTransactionV2(senderPublicKey, recipient, amount, fee, timestamp, Collections.unmodifiableList(newProofs));
    }
}
