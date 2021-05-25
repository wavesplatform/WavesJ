package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.LeaseStatus;

import java.util.Objects;

public class LeaseTransactionInfo extends TransactionInfo {

    private final LeaseStatus status;

    public LeaseTransactionInfo(LeaseTransaction tx, ApplicationStatus applicationStatus, int height, LeaseStatus status) {
        super(tx, applicationStatus, height);
        this.status = status;
    }

    public LeaseTransaction tx() {
        return (LeaseTransaction) super.tx();
    }

    public LeaseStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LeaseTransactionInfo that = (LeaseTransactionInfo) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }

    @Override
    public String toString() {
        return "LeaseTransactionInfo{" +
                "status=" + status +
                "} " + super.toString();
    }
}
