package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.Common;

import java.util.Objects;

public class TransactionWithStatus {

    private final Transaction tx;
    private final ApplicationStatus applicationStatus;

    public TransactionWithStatus(Transaction tx, ApplicationStatus applicationStatus) {
        this.tx = Common.notNull(tx, "Transaction");
        this.applicationStatus = applicationStatus == null ? ApplicationStatus.SUCCEEDED : applicationStatus;
    }

    public ApplicationStatus applicationStatus() {
        return applicationStatus;
    }

    public Transaction tx() {
        return tx;
    }

    public <T extends Transaction> T tx(Class<T> clazz) {
        return (T) tx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionWithStatus that = (TransactionWithStatus) o;
        return Objects.equals(tx, that.tx) &&
                applicationStatus == that.applicationStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tx, applicationStatus);
    }

    @Override
    public String toString() {
        return "TransactionWithStatus{" +
                "tx=" + tx +
                ", applicationStatus=" + applicationStatus +
                '}';
    }

}
