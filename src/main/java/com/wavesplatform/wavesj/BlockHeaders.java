package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import im.mak.waves.transactions.account.Address;
import im.mak.waves.transactions.account.PublicKey;
import im.mak.waves.transactions.common.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a block headers.
 */
@SuppressWarnings("unused")
public class BlockHeaders {
    private final int version;
    private final long timestamp;
    private final Id reference;
    private long baseTarget;
    private String generationSignature;
    private final String transactionsRoot; //todo common type
    private final Id id;
    private final List<Integer> features;
    private final long desiredReward;
    private final Address generator;
    private final PublicKey generatorPublicKey;
    private final String signature; //todo common type
    private final int size;
    private final int transactionsCount;
    private final int height;
    private final long totalFee;
    private final long reward;
    private final Id vrf; //todo common binary type instead of Id

    @JsonCreator
    public BlockHeaders( //todo what about old versions?
            @JsonProperty("version") int version,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("reference") Id reference,
            @JsonProperty("transactionsRoot") String transactionsRoot,
            @JsonProperty("id") Id id,
            @JsonProperty("features") List<Integer> features,
            @JsonProperty("desiredReward") long desiredReward,
            @JsonProperty("generator") Address generator,
            @JsonProperty("generatorPublicKey") PublicKey generatorPublicKey,
            @JsonProperty("signature") String signature,
            @JsonProperty("blocksize") int size,
            @JsonProperty("transactionsCount") int transactionsCount,
            @JsonProperty("height") int height,
            @JsonProperty("totalFee") long totalFee,
            @JsonProperty("reward") long reward,
            @JsonProperty("VRF") Id vrf) {
        this.height = height;
        this.version = version;
        this.timestamp = timestamp;
        this.reference = Common.notNull(reference, "Reference");
        this.transactionsRoot = height == 1 ? "" : Common.notNull(transactionsRoot, "TransactionsRoot");
        this.id = height == 1 ? Id.as("11111111111111111111111111111111") : Common.notNull(id, "Id");
        this.features = height == 1 ? new ArrayList<>() : Common.notNull(features, "Features");
        this.desiredReward = desiredReward;
        this.generator = Common.notNull(generator, "Generator");
        this.generatorPublicKey = Common.notNull(generatorPublicKey, "GeneratorPublicKey");
        this.signature = Common.notNull(signature, "Signature");
        this.size = size;
        this.transactionsCount = transactionsCount;
        this.totalFee = totalFee;
        this.reward = reward;
        this.vrf = vrf == null ? Id.as("11111111111111111111111111111111") : vrf;
    }

    @JsonProperty("nxt-consensus")
    private void nxtConsensus(Map<String, Object> nxtConsensus) {
        this.baseTarget = (int) nxtConsensus.get("base-target");
        this.generationSignature = (String) nxtConsensus.get("generation-signature");
    }

    public int version() {
        return version;
    }

    public long timestamp() {
        return timestamp;
    }

    public Id reference() {
        return reference;
    }

    public long baseTarget() {
        return baseTarget;
    }

    public String generationSignature() {
        return generationSignature;
    }

    public String transactionsRoot() {
        return transactionsRoot;
    }

    public Id id() {
        return id;
    }

    public List<Integer> features() {
        return features;
    }

    public long desiredReward() {
        return desiredReward;
    }

    public Address generator() {
        return generator;
    }

    public PublicKey generatorPublicKey() {
        return generatorPublicKey;
    }

    public String signature() {
        return signature;
    }

    public int size() {
        return size;
    }

    public int transactionsCount() {
        return transactionsCount;
    }

    public int height() {
        return height;
    }

    public long totalFee() {
        return totalFee;
    }

    public long reward() {
        return reward;
    }

    public Id vrf() {
        return vrf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockHeaders that = (BlockHeaders) o;
        return version == that.version &&
                timestamp == that.timestamp &&
                baseTarget == that.baseTarget &&
                desiredReward == that.desiredReward &&
                size == that.size &&
                transactionsCount == that.transactionsCount &&
                height == that.height &&
                totalFee == that.totalFee &&
                reward == that.reward &&
                Objects.equals(reference, that.reference) &&
                Objects.equals(generationSignature, that.generationSignature) &&
                Objects.equals(transactionsRoot, that.transactionsRoot) &&
                Objects.equals(id, that.id) &&
                Objects.equals(features, that.features) &&
                Objects.equals(generator, that.generator) &&
                Objects.equals(generatorPublicKey, that.generatorPublicKey) &&
                Objects.equals(signature, that.signature) &&
                Objects.equals(vrf, that.vrf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, timestamp, reference, baseTarget, generationSignature, transactionsRoot, id, features, desiredReward, generator, generatorPublicKey, signature, size, transactionsCount, height, totalFee, reward, vrf);
    }

    @Override
    public String toString() {
        return "BlockHeader{" +
                "version=" + version +
                ", timestamp=" + timestamp +
                ", reference=" + reference +
                ", baseTarget=" + baseTarget +
                ", generationSignature='" + generationSignature + '\'' +
                ", transactionsRoot='" + transactionsRoot + '\'' +
                ", id=" + id +
                ", features=" + features +
                ", desiredReward=" + desiredReward +
                ", generator=" + generator +
                ", generatorPublicKey=" + generatorPublicKey +
                ", signature='" + signature + '\'' +
                ", size=" + size +
                ", transactionsCount=" + transactionsCount +
                ", height=" + height +
                ", totalFee=" + totalFee +
                ", reward=" + reward +
                ", vrf=" + vrf +
                '}';
    }
}
