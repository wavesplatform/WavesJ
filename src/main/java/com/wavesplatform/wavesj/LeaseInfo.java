package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Recipient;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public class LeaseInfo {

    private final Id id;
    private final Id originTransactionId;
    private final Address sender;
    private final Recipient recipient;
    private final long amount;
    private final int height;
    private final LeaseStatus status;
    private final int cancelHeight;
    private final Id cancelTransactionId;

    @JsonCreator
    public LeaseInfo(
            @JsonProperty("id") Id id,
            @JsonProperty("originTransactionId") Id originTransactionId,
            @JsonProperty("sender") Address sender,
            @JsonProperty("recipient") Recipient recipient,
            @JsonProperty("amount") long amount,
            @JsonProperty("height") int height,
            @JsonProperty("status") LeaseStatus status,
            @JsonProperty("cancelHeight") int cancelHeight,
            @JsonProperty("cancelTransactionId") Id cancelTransactionId) {
        this.id = id;
        this.originTransactionId = originTransactionId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.height = height;
        this.status = status;
        this.cancelHeight = cancelHeight;
        this.cancelTransactionId = cancelTransactionId;
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

    public OptionalInt cancelHeight() {
        return cancelHeight > 0 ? OptionalInt.of(cancelHeight) : OptionalInt.empty();
    }

    public Optional<Id> cancelTransactionId() {
        return Optional.ofNullable(cancelTransactionId);
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
                && Objects.equals(originTransactionId, that.originTransactionId)
                && cancelHeight == that.cancelHeight
                && Objects.equals(cancelTransactionId, that.cancelTransactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originTransactionId, sender, recipient, amount, height, status, cancelHeight, cancelTransactionId);
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
                ", cancelHeight=" + cancelHeight +
                ", cancelTransactionId=" + cancelTransactionId +
                '}';
    }

}
