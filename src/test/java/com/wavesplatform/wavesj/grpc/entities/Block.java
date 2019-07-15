package com.wavesplatform.wavesj.grpc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(value = { "blocksize", "fee", "transactions", "totalFee" })
public
class Block {

    private int height;

    private String signature;

    private int transactionCount;

    private String chainId;

    private String reference;

    private long timestamp;

    private int version;

    private String generator;

    private List<String> features;

    private long baseTarget;

    private String generationSignature;

    @JsonProperty("nxt-consensus")
    private void unpackFromNestedObject(Map<String, String> nxtConsensus) {
        baseTarget = Long.parseLong(nxtConsensus.get("base-target"));
        generationSignature = nxtConsensus.get("generation-signature");
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public long getBaseTarget() {
        return baseTarget;
    }

    public void setBaseTarget(long baseTarget) {
        this.baseTarget = baseTarget;
    }

    public String getGenerationSignature() {
        return generationSignature;
    }

    public void setGenerationSignature(String generationSignature) {
        this.generationSignature = generationSignature;
    }
}
