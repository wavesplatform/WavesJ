package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@SuppressWarnings("unused")
public class BlockchainRewards {

    private final int height;
    private final long totalWavesAmount;
    private final long currentReward;
    private final long minIncrement;
    private final int term;
    private final int nextCheck;
    private final int votingIntervalStart;
    private final int votingInterval;
    private final int votingThreshold;
    private final Votes votes;

    @JsonCreator
    private BlockchainRewards(
            @JsonProperty("height") int height,
            @JsonProperty("currentReward") long currentReward,
            @JsonProperty("totalWavesAmount") long totalWavesAmount,
            @JsonProperty("minIncrement") long minIncrement,
            @JsonProperty("term") int term,
            @JsonProperty("nextCheck") int nextCheck,
            @JsonProperty("votingIntervalStart") int votingIntervalStart,
            @JsonProperty("votingInterval") int votingInterval,
            @JsonProperty("votingThreshold") int votingThreshold,
            @JsonProperty("votes") Votes votes) {
        this.height = height;
        this.currentReward = currentReward;
        this.totalWavesAmount = totalWavesAmount;
        this.minIncrement = minIncrement;
        this.term = term;
        this.nextCheck = nextCheck;
        this.votingIntervalStart = votingIntervalStart;
        this.votingInterval = votingInterval;
        this.votingThreshold = votingThreshold;
        this.votes = Common.notNull(votes, "Votes");
    }


    public int height() {
        return height;
    }

    public long totalWavesAmount() {
        return totalWavesAmount;
    }

    public long currentReward() {
        return currentReward;
    }

    public long minIncrement() {
        return minIncrement;
    }

    public int term() {
        return term;
    }

    public int nextCheck() {
        return nextCheck;
    }

    public int votingIntervalStart() {
        return votingIntervalStart;
    }

    public int votingInterval() {
        return votingInterval;
    }

    public int votingThreshold() {
        return votingThreshold;
    }

    public Votes votes() {
        return votes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockchainRewards that = (BlockchainRewards) o;
        return height == that.height &&
                totalWavesAmount == that.totalWavesAmount &&
                currentReward == that.currentReward &&
                minIncrement == that.minIncrement &&
                term == that.term &&
                nextCheck == that.nextCheck &&
                votingIntervalStart == that.votingIntervalStart &&
                votingInterval == that.votingInterval &&
                votingThreshold == that.votingThreshold &&
                Objects.equals(votes, that.votes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, totalWavesAmount, currentReward, minIncrement, term, nextCheck,
                votingIntervalStart, votingInterval, votingThreshold, votes);
    }

    @Override
    public String toString() {
        return "BlockchainRewards{" +
                "height=" + height +
                ", totalWavesAmount=" + totalWavesAmount +
                ", currentReward=" + currentReward +
                ", minIncrement=" + minIncrement +
                ", term=" + term +
                ", nextCheck=" + nextCheck +
                ", votingIntervalStart=" + votingIntervalStart +
                ", votingInterval=" + votingInterval +
                ", votingThreshold=" + votingThreshold +
                ", votes=" + votes +
                '}';
    }

}
