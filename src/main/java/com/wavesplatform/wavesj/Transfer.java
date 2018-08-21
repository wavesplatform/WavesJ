package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Transfer {
    private final String recipient;
    private final long amount;

    @JsonCreator
    public Transfer(@JsonProperty("recipient") String recipient, @JsonProperty("amount") long amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        if (getAmount() != transfer.getAmount()) return false;
        return getRecipient() != null ? getRecipient().equals(transfer.getRecipient()) : transfer.getRecipient() == null;
    }

    @Override
    public int hashCode() {
        int result = getRecipient() != null ? getRecipient().hashCode() : 0;
        result = 31 * result + (int) (getAmount() ^ (getAmount() >>> 32));
        return result;
    }
}
