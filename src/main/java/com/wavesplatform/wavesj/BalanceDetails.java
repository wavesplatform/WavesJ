package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import im.mak.waves.transactions.account.Address;

import java.util.Objects;

@SuppressWarnings("unused")
public class BalanceDetails {

    private final Address address;
    private final long available;
    private final long regular;
    private final long generating;
    private final long effective;

    @JsonCreator
    public BalanceDetails(@JsonProperty("address") Address address,
                          @JsonProperty("available") long available,
                          @JsonProperty("regular") long regular,
                          @JsonProperty("generating") long generating,
                          @JsonProperty("effective") long effective) {
        this.address = Common.notNull(address, "Address");
        this.available = available;
        this.regular = regular;
        this.generating = generating;
        this.effective = effective;
    }

    public Address address() {
        return address;
    }

    public long available() {
        return available;
    }

    public long regular() {
        return regular;
    }

    public long generating() {
        return generating;
    }

    public long effective() {
        return effective;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceDetails that = (BalanceDetails) o;
        return this.available == that.available &&
                this.regular == that.regular &&
                this.generating == that.generating &&
                this.effective == that.effective &&
                this.address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, available, regular, generating, effective);
    }

    @Override
    public String toString() {
        return "BalanceDetails{" +
                "address=" + address.toString() +
                ", available=" + available +
                ", regular=" + regular +
                ", generating=" + generating +
                ", effective=" + effective +
                '}';
    }

}
