package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.Common;

import java.util.Objects;

public class TransactionWithStatus<T extends Transaction> {

    private final T tx;
    private final ApplicationStatus applicationStatus;

    public TransactionWithStatus(T tx, ApplicationStatus applicationStatus) {
        this.tx = Common.notNull(tx, "Transaction");
        this.applicationStatus = applicationStatus == null ? ApplicationStatus.SUCCEEDED : applicationStatus;
    }

    public ApplicationStatus applicationStatus() {
        return applicationStatus;
    }

    public T tx() {
        return tx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionWithStatus<?> that = (TransactionWithStatus<?>) o;
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
