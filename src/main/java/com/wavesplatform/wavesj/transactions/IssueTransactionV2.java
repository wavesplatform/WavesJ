package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

public class IssueTransactionV2 extends TransactionWithProofs implements IssueTransaction {
    public static final byte ISSUE = 3;

    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String name;
    private final String description;
    private final long quantity;
    private final byte decimals;
    private final boolean reissuable;
    private final String script;
    private final long fee;
    private final long timestamp;

    public IssueTransactionV2(PrivateKeyAccount senderPublicKey,
                              byte chainId,
                              String name,
                              String description,
                              long quantity,
                              byte decimals,
                              boolean reissuable,
                              String script,
                              long fee,
                              long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.script = script;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @JsonCreator
    public IssueTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("chainId") byte chainId,
                              @JsonProperty("name") String name,
                              @JsonProperty("description") String description,
                              @JsonProperty("quantity") long quantity,
                              @JsonProperty("decimals") byte decimals,
                              @JsonProperty("reissuable") boolean reissuable,
                              @JsonProperty("script") String script,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("proofs") List<ByteString> proofs) {
        super(proofs);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.script = script;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte getChainId() {
        return chainId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getQuantity() {
        return quantity;
    }

    public byte getDecimals() {
        return decimals;
    }

    public boolean isReissuable() {
        return reissuable;
    }

    public String getScript() {
        return script;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(10 * KBYTE);
        buf.put(senderPublicKey.getPublicKey());
        putString(buf, name);
        putString(buf, description);
        buf.putLong(quantity)
                .put(decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee)
                .putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return ISSUE;
    }

    @Override
    public byte getVersion() {
        return Transaction.V2;
    }

    public IssueTransactionV2 withProof(int index, ByteString proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<ByteString> newProofs = new ArrayList<ByteString>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add(ByteString.EMPTY);
        }
        newProofs.set(index, proof);
        return new IssueTransactionV2(senderPublicKey, chainId, name, description, quantity, decimals, reissuable, script, fee, timestamp, Collections.unmodifiableList(newProofs));
    }
}
