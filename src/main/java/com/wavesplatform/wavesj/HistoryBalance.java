package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@SuppressWarnings("unused")
public class HistoryBalance {

    private final int height;
    private final long balance;

    @JsonCreator
    public HistoryBalance(@JsonProperty("height") int height,
                          @JsonProperty("balance") long balance) {
        this.height = height;
        this.balance = balance;
    }

    public int height() {
        return height;
    }

    public long balance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryBalance that = (HistoryBalance) o;
        return height == that.height &&
                balance == that.balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, balance);
    }

    @Override
    public String toString() {
        return "HistoryBalance{" +
                "height=" + height +
                ", balance=" + balance +
                '}';
    }

}
