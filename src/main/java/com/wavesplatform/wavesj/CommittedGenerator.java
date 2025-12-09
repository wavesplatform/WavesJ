package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Id;

import java.util.Objects;

public class CommittedGenerator {

    private final Address address;
    private final long balance;
    private final Id transactionId;

    @JsonCreator
    public CommittedGenerator(@JsonProperty("address") Address address,
                              @JsonProperty("balance") long balance,
                              @JsonProperty("transactionId") Id transactionId) {
        this.address = address;
        this.balance = balance;
        this.transactionId = transactionId;
    }

    public Address address(){
        return address;
    }

    public long balance() {
        return balance;
    }

    public Id transactionId(){
        return transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommittedGenerator that = (CommittedGenerator) o;
        return address == that.address &&
                balance == that.balance &&
                transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, balance, transactionId);
    }

    @Override
    public String toString() {
        return "CommittedGenerator{" +
                "address=" + address.toString() +
                ", balance=" + balance +
                ", transactionId=" + transactionId +
                '}';
    }
}
