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
    private final Collection<Transaction> transactions;

    @JsonCreator
    private Block(
            @JsonProperty("version") int version,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("signature") String signature,
            @JsonProperty("blocksize") int size,
            @JsonProperty("fee") long fee,
            @JsonProperty("height") int height,
            @JsonProperty("transactions") Collection<Transaction> transactions) {
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

    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Block))
            return false;

        final Block block = (Block) o;

        if (getVersion() != block.getVersion())
            return false;
        if (getTimestamp() != block.getTimestamp())
            return false;
        if (getSize() != block.getSize())
            return false;
        if (getFee() != block.getFee())
            return false;
        if (getHeight() != block.getHeight())
            return false;
        if (getSignature() != null ? !getSignature().equals(block.getSignature()) : block.getSignature() != null)
            return false;
        return getTransactions() != null ? getTransactions().equals(block.getTransactions()) : block.getTransactions() == null;
    }

    @Override
    public int hashCode() {
        int result = getVersion();
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        result = 31 * result + (getSignature() != null ? getSignature().hashCode() : 0);
        result = 31 * result + getSize();
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + getHeight();
        result = 31 * result + (getTransactions() != null ? getTransactions().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Block{" +
                "version=" + version +
                ", timestamp=" + timestamp +
                ", signature='" + signature + '\'' +
                ", size=" + size +
                ", fee=" + fee +
                ", height=" + height +
                ", transactions=" + transactions +
                '}';
    }
}
