package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.wavesj.info.TransactionWithStatus;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a block.
 */
@SuppressWarnings("unused")
public class Block extends BlockHeaders {

    private final List<TransactionWithStatus> transactions;
    private final long fee;

    @JsonCreator
    public Block(
            @JsonProperty("version") int version,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("reference") Base58String reference,
            @JsonProperty("transactionsRoot") Base58String transactionsRoot,
            @JsonProperty("id") Base58String id,
            @JsonProperty("features") List<Integer> features,
            @JsonProperty("desiredReward") long desiredReward,
            @JsonProperty("generator") Address generator,
            @JsonProperty("signature") Base58String signature,
            @JsonProperty("blocksize") int size,
            @JsonProperty("transactionCount") int transactionsCount,
            @JsonProperty("height") int height,
            @JsonProperty("totalFee") long totalFee,
            @JsonProperty("reward") long reward,
            @JsonProperty("VRF") Base58String vrf,
            @JsonProperty("fee") long fee,
            @JsonProperty("transactions") List<TransactionWithStatus> transactions) {
        super(version, timestamp, reference, transactionsRoot, id, features, desiredReward, generator,
                signature, size, transactionsCount, height, totalFee, reward, vrf);
        this.fee = fee;
        this.transactions = Common.notNull(transactions, "Transactions");
    }

    public long fee() {
        return fee;
    }

    public List<TransactionWithStatus> transactions() {
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
                "transactions=" + transactions +
                ", fee=" + fee +
                "} " + super.toString();
    }

}
