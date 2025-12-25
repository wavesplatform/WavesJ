package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Base58String;

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
    private final Base58String reference;
    private long baseTarget;
    private Base58String generationSignature;
    private final Base58String transactionsRoot;
    private final Base58String id;
    private final List<Integer> features;
    private final long desiredReward;
    private final Address generator;
    private final PublicKey generatorPublicKey;
    private final Base58String signature;
    private final int size;
    private final int transactionsCount;
    private final int height;
    private final long totalFee;
    private final long reward;
    private final Base58String vrf;
    private final ChallengedHeader challengedHeader;
    private final FinalizationVoting finalizationVoting;
    private final Base58String stateHash;
    private final Map<Address, Long> rewardShares;

    @JsonCreator
    public BlockHeaders(
            @JsonProperty("version") int version,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("reference") Base58String reference,
            @JsonProperty("transactionsRoot") Base58String transactionsRoot,
            @JsonProperty("id") Base58String id,
            @JsonProperty("features") List<Integer> features,
            @JsonProperty("desiredReward") long desiredReward,
            @JsonProperty("generator") Address generator,
            @JsonProperty("generatorPublicKey") PublicKey generatorPublicKey,
            @JsonProperty("stateHash") Base58String stateHash,
            @JsonProperty("rewardShares") Map<Address, Long> rewardShares,
            @JsonProperty("signature") Base58String signature,
            @JsonProperty("blocksize") int size,
            @JsonProperty("transactionCount") int transactionsCount,
            @JsonProperty("height") int height,
            @JsonProperty("totalFee") long totalFee,
            @JsonProperty("reward") long reward,
            @JsonProperty("VRF") Base58String vrf,
            @JsonProperty("challengedHeader") ChallengedHeader challengedHeader,
            @JsonProperty("finalizationVoting") FinalizationVoting finalizationVoting) {
        this.height = height;
        this.version = version;
        this.timestamp = timestamp;
        this.reference = Common.notNull(reference, "Reference");
        this.generator = Common.notNull(generator, "Generator");
        this.generatorPublicKey = generatorPublicKey;
        this.stateHash = stateHash == null ? Base58String.empty() : stateHash;
        this.rewardShares = rewardShares == null ? Map.of() : rewardShares;
        this.signature = Common.notNull(signature, "Signature");
        this.id = id == null ? this.signature : id;
        this.vrf = vrf == null ? Base58String.empty() : vrf;
        this.transactionsRoot = transactionsRoot == null ? Base58String.empty() : transactionsRoot;
        this.size = size;
        this.transactionsCount = transactionsCount;
        this.totalFee = totalFee;
        this.reward = reward;
        this.desiredReward = desiredReward;
        this.features = features == null ? new ArrayList<>() : features;
        this.challengedHeader = challengedHeader;
        this.finalizationVoting = finalizationVoting;
    }

    @JsonProperty("nxt-consensus")
    private void nxtConsensus(Map<String, Object> nxtConsensus) {
        Object baseTargetObj = nxtConsensus.get("base-target");
        this.baseTarget = ((Number) nxtConsensus.get("base-target")).longValue();
        this.generationSignature = new Base58String((String) nxtConsensus.get("generation-signature"));
    }

    public ChallengedHeader challengedHeader() {
        return challengedHeader;
    }

    public FinalizationVoting finalizationVoting() {
        return finalizationVoting;
    }

    public int version() {
        return version;
    }

    public long timestamp() {
        return timestamp;
    }

    public Base58String reference() {
        return reference;
    }

    public long baseTarget() {
        return baseTarget;
    }

    public Base58String generationSignature() {
        return generationSignature;
    }

    public Base58String transactionsRoot() {
        return transactionsRoot;
    }

    public Base58String id() {
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

    public Base58String stateHash() {
        return stateHash;
    }

    public Map<Address, Long> rewardShares() {
        return rewardShares;
    }

    public Base58String signature() {
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

    public Base58String vrf() {
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
                Objects.equals(stateHash, that.stateHash) &&
                Objects.equals(rewardShares, that.rewardShares) &&
                Objects.equals(signature, that.signature) &&
                Objects.equals(vrf, that.vrf) &&
                Objects.equals(challengedHeader, that.challengedHeader) &&
                Objects.equals(finalizationVoting, that.finalizationVoting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                version,
                timestamp,
                reference,
                baseTarget,
                generationSignature,
                transactionsRoot,
                id,
                features,
                desiredReward,
                generator,
                generatorPublicKey,
                stateHash,
                rewardShares,
                signature,
                size,
                transactionsCount,
                height,
                totalFee,
                reward,
                vrf,
                challengedHeader,
                finalizationVoting
        );
    }

    @Override
    public String toString() {
        return "BlockHeader{" +
                "id=" + id +
                ", height=" + height +
                ", timestamp=" + timestamp +
                ", generator=" + generator +
                ", generatorPublicKey=" + generatorPublicKey +
                ", version=" + version +
                ", reference=" + reference +
                ", baseTarget=" + baseTarget +
                ", generationSignature=" + generationSignature +
                (stateHash != null ? ", stateHash=" + stateHash : "") +
                ", vrf=" + vrf +
                ", features=" + features +
                ", signature=" + signature +
                ", desiredReward=" + desiredReward +
                ", reward=" + reward +
                (rewardShares != null ? ", rewardShares=" + rewardShares : "") +
                ", totalFee=" + totalFee +
                ", transactionsCount=" + transactionsCount +
                ", transactionsRoot=" + transactionsRoot +
                ", size=" + size +
                (challengedHeader != null ? ", challengedHeader=" + challengedHeader : "") +
                (finalizationVoting != null ? ", finalizationVoting=" + finalizationVoting : "") +
                '}';
    }
}
