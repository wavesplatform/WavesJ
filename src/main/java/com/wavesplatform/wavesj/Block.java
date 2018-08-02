package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;

/**
 * This class represents a block.
 */
public class Block {
    private final int version;
    private final long timestamp;
    private final String signature;
    private final int size;
    private final long fee;
    private final int height;
    private final Collection<ProofedObject<Transaction>> transactions;

    @JsonCreator
    private Block(
            @JsonProperty("version") int version,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("signature") String signature,
            @JsonProperty("blocksize") int size,
            @JsonProperty("fee") long fee,
            @JsonProperty("height") int height,
            @JsonProperty("transactions") Collection<ProofedObject<Transaction>> transactions) {
        this.version = version;
        this.timestamp = timestamp;
        this.signature = signature;
        this.size = size;
        this.fee = fee;
        this.height = height;
        this.transactions = transactions != null ? Collections.unmodifiableCollection(transactions) : null;
    }

    public int getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public int getSize() {
        return size;
    }

    public long getFee() {
        return fee;
    }

    public int getHeight() {
        return height;
    }

    public Collection<ProofedObject<Transaction>> getTransactions() {
        return transactions;
    }
}
