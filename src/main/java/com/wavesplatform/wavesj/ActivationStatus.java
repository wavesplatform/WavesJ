package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class ActivationStatus {

    private final int height;
    private final int votingInterval;
    private final int votingThreshold;
    private final int nextCheck;

    private final List<Feature> features;

    @JsonCreator
    public ActivationStatus(
            @JsonProperty("height") int height,
            @JsonProperty("votingInterval") int votingInterval,
            @JsonProperty("votingThreshold") int votingThreshold,
            @JsonProperty("nextCheck") int nextCheck,
            @JsonProperty("features") List<Feature> features
    ) {
        this.height = height;
        this.votingInterval = votingInterval;
        this.votingThreshold = votingThreshold;
        this.nextCheck = nextCheck;
        this.features = features;
    }

    public int getHeight() {
        return height;
    }

    public int getVotingInterval() {
        return votingInterval;
    }

    public int getVotingThreshold() {
        return votingThreshold;
    }

    public int getNextCheck() {
        return nextCheck;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivationStatus that = (ActivationStatus) o;
        return height == that.height && votingInterval == that.votingInterval && votingThreshold == that.votingThreshold && nextCheck == that.nextCheck && Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, votingInterval, votingThreshold, nextCheck, features);
    }

    @Override
    public String toString() {
        return "ActivationStatus{" +
                "height=" + height +
                ", votingInterval=" + votingInterval +
                ", votingThreshold=" + votingThreshold +
                ", nextCheck=" + nextCheck +
                ", features=" + features +
                '}';
    }
}
