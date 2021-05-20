package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Recipient;
import com.wavesplatform.wavesj.LeaseStatus;

import java.util.Objects;

public class LeaseInfo {

    private final Id id;
    private final Id originTransactionId;
    private final Address sender;
    private final Recipient recipient;
    private final long amount;
    private final int height;
    private final LeaseStatus status;

    @JsonCreator
    public LeaseInfo(
            @JsonProperty("id") Id id,
            @JsonProperty("originTransactionId") Id originTransactionId,
            @JsonProperty("sender") Address sender,
            @JsonProperty("recipient") Recipient recipient,
            @JsonProperty("amount") long amount,
            @JsonProperty("height") int height,
            @JsonProperty("status") LeaseStatus status) {
        this.id = id;
        this.originTransactionId = originTransactionId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.height = height;
        this.status = status;
    }

    public Id id() {
        return id;
    }

    public Id originTransactionId() {
        return originTransactionId;
    }

    public Address sender() {
        return sender;
    }

    public Recipient recipient() {
        return recipient;
    }

    public long amount() {
        return amount;
    }

    public int height() {
        return height;
    }

    public LeaseStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaseInfo that = (LeaseInfo) o;
        return Objects.equals(id, that.id)
                && status == that.status
                && height == that.height
                && amount == that.amount
                && Objects.equals(sender, that.sender)
                && Objects.equals(recipient, that.recipient)
                && Objects.equals(originTransactionId, that.originTransactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originTransactionId, sender, recipient, amount, height, status);
    }

    @Override
    public String toString() {
        return "LeaseInfo{" +
                "id=" + id +
                ", originTransactionId=" + originTransactionId +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", amount=" + amount +
                ", height=" + height +
                ", status=" + status +
                '}';
    }

}
