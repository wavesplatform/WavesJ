package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import im.mak.waves.transactions.account.Address;
import im.mak.waves.transactions.account.PublicKey;
import im.mak.waves.transactions.common.Id;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a block.
 */
@SuppressWarnings("unused")
public class Block extends BlockHeaders {

    private final long fee;
    private final List<TransactionInfo> transactions;

    @JsonCreator
    public Block(
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
            @JsonProperty("VRF") Id vrf,
            @JsonProperty("fee") long fee,
            @JsonProperty("transactions") List<TransactionInfo> transactions) {
        super(version, timestamp, reference, transactionsRoot, id, features, desiredReward, generator,
                generatorPublicKey, signature, size, transactionsCount, height, totalFee, reward, vrf);
        this.fee = fee;
        this.transactions = Common.notNull(transactions, "Transactions");

        //transactions in block don't have height
        for (int i = 0; i < this.transactions.size(); i++) {
            TransactionInfo info = this.transactions.get(i);
            if (info.height() == 0)
                this.transactions.set(i, new TransactionInfo(info.tx(), info.applicationStatus(), this.height()));
        }
    }

    public long fee() {
        return fee;
    }

    public List<TransactionInfo> transactions() {
        return transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Block block = (Block) o;
        return fee == block.fee &&
                Objects.equals(transactions, block.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fee, transactions);
    }

    @Override
    public String toString() {
        return "Block{" +
                "version=" + version() +
                ", timestamp=" + timestamp() +
                ", reference=" + reference() +
                ", baseTarget=" + baseTarget() +
                ", generationSignature='" + generationSignature() + '\'' +
                ", transactionsRoot='" + transactionsRoot() + '\'' +
                ", id=" + id() +
                ", features=" + features() +
                ", desiredReward=" + desiredReward() +
                ", generator=" + generator() +
                ", generatorPublicKey=" + generatorPublicKey() +
                ", signature='" + signature() + '\'' +
                ", size=" + size() +
                ", transactionsCount=" + transactionsCount() +
                ", height=" + height() +
                ", totalFee=" + totalFee() +
                ", reward=" + reward() +
                ", vrf=" + vrf() +
                ", fee=" + fee +
                ", transactions=" + transactions +
                '}';
    }
}
