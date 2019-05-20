package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class represents a block.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockHeader {
    private final int version;
    private final long timestamp;
    private final String signature;
    private final String generator;
    private final int size;
    private final long fee;
    private final int height;
    private final int transactionCount;
    private final NxtConsensus consensus;

    @JsonCreator
    private BlockHeader(
            @JsonProperty("version") int version,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("signature") String signature,
            @JsonProperty("blocksize") int size,
            @JsonProperty("generator") String generator,
            @JsonProperty("fee") long fee,
            @JsonProperty("height") int height,
            @JsonProperty("transactionCount") int transactionCount,
            @JsonProperty("nxt-consensus") NxtConsensus consensus) {
        this.version = version;
        this.timestamp = timestamp;
        this.signature = signature;
        this.generator = generator;
        this.size = size;
        this.fee = fee;
        this.height = height;
        this.transactionCount = transactionCount;
        this.consensus = consensus;
    }


    public int getTransactionCount() {
        return transactionCount;
    }

    public int getHeight() {
        return height;
    }

    public long getFee() {
        return fee;
    }

    public int getSize() {
        return size;
    }

    public String getGenerator() {
        return generator;
    }

    public String getSignature() {
        return signature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getVersion() {
        return version;
    }

    public NxtConsensus getConsensus(){
        return consensus;
    }
}
