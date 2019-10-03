package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rewards {

    private int height;
    private long totalWavesAmount;
    private long currentReward;
    private long minIncrement;
    private long term;
    private int nextCheck;
    private int votingIntervalStart;
    private int votingInterval;
    private int votingThreshold;
    private Votes votes;

    @JsonCreator
    private Rewards(
            @JsonProperty("height") int height,
            @JsonProperty("currentReward") long currentReward,
            @JsonProperty("totalWavesAmount") long totalWavesAmount,
            @JsonProperty("minIncrement") long minIncrement,
            @JsonProperty("term") long term,
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
        this.votes = votes;
    }


    public int getHeight() {
        return height;
    }

    public long getTotalWavesAmount() {
        return totalWavesAmount;
    }

    public long getCurrentReward() {
        return currentReward;
    }

    public long getMinIncrement() {
        return minIncrement;
    }

    public long getTerm() {
        return term;
    }

    public int getNextCheck() {
        return nextCheck;
    }

    public int getVotingIntervalStart() {
        return votingIntervalStart;
    }

    public int getVotingInterval() {
        return votingInterval;
    }

    public int getVotingThreshold() {
        return votingThreshold;
    }

    public Votes getVotes() {
        return votes;
    }
}
