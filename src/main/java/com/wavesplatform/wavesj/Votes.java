package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@SuppressWarnings("unused")
public class Votes {

    private final int increase;
    private final int decrease;

    @JsonCreator
    private Votes(
            @JsonProperty("increase") int increase,
            @JsonProperty("decrease") int decrease) {
        this.increase = increase;
        this.decrease = decrease;
    }

    public int increase() {
        return increase;
    }

    public int decrease() {
        return decrease;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Votes votes = (Votes) o;
        return increase == votes.increase &&
                decrease == votes.decrease;
    }

    @Override
    public int hashCode() {
        return Objects.hash(increase, decrease);
    }

    @Override
    public String toString() {
        return "Votes{" +
                "increase=" + increase +
                ", decrease=" + decrease +
                '}';
    }
}
