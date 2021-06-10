package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.GenesisTransaction;
import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.wavesj.ApplicationStatus;

import java.util.Objects;

public abstract class TransactionInfo extends TransactionWithStatus {

    private final int height;

    public TransactionInfo(Transaction tx,
                           ApplicationStatus applicationStatus,
                           int height) {
        super(tx, applicationStatus);
        this.height = height == 0 && tx.type() == GenesisTransaction.TYPE ? 1 : height;
    }

    public int height() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TransactionInfo that = (TransactionInfo) o;
        return height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), height);
    }

    @Override
    public String toString() {
        return "TransactionInfo{" +
                "height=" + height +
                "} " + super.toString();
    }

}
