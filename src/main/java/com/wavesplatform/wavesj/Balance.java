package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;

import java.util.Objects;

public class Balance {
    private final Address address;
    private final long balance;

    public Balance(@JsonProperty("id") Address address,
                   @JsonProperty("balance") long balance) {
        this.address = Common.notNull(address, "Id");
        this.balance = balance;
    }

    public Address getAddress() {
        return address;
    }

    public long getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Balance)) return false;
        Balance balance1 = (Balance) o;
        return getBalance() == balance1.getBalance() &&
                Objects.equals(getAddress(), balance1.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getBalance());
    }

    @Override
    public String toString() {
        return "Balance{" +
                "address=" + address +
                ", balance=" + balance +
                '}';
    }
}
