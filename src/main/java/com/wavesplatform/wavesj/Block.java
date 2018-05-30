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
public class Block {
    public final int version;
    public final long timestamp;
    public final String signature;
    public final int size;
    public final long fee;
    public final int height;
    public final List<Map<String, Object>> transactions;

    @JsonCreator
    private Block(
            @JsonProperty("version") int version,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("signature") String signature,
            @JsonProperty("blocksize") int size,
            @JsonProperty("fee") long fee,
            @JsonProperty("height") int height,
            @JsonProperty("transactions") List<Map<String, Object>> transactions)
    {
        this.version = version;
        this.timestamp = timestamp;
        this.signature = signature;
        this.size = size;
        this.fee = fee;
        this.height = height;
        this.transactions = Collections.unmodifiableList(transactions);
    }
}
