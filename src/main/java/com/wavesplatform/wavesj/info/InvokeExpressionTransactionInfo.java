package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.InvokeExpressionTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.StateChanges;

import java.util.Objects;

public class InvokeExpressionTransactionInfo extends TransactionInfo {

    private final StateChanges stateChanges;

    public InvokeExpressionTransactionInfo(InvokeExpressionTransaction tx, ApplicationStatus applicationStatus, int height, StateChanges stateChanges) {
        super(tx, applicationStatus, height);
        this.stateChanges = stateChanges;
    }

    public InvokeExpressionTransaction tx() {
        return (InvokeExpressionTransaction) super.tx();
    }

    public StateChanges stateChanges() {
        return stateChanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InvokeExpressionTransactionInfo that = (InvokeExpressionTransactionInfo) o;
        return Objects.equals(stateChanges, that.stateChanges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stateChanges);
    }

    @Override
    public String toString() {
        return "InvokeExpressionTransactionInfo{" +
                "stateChanges=" + stateChanges +
                "} " + super.toString();
    }
}
