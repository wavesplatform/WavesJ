package com.wavesplatform.wavesj;

import com.wavesplatform.transactions.Transaction;

import java.util.Objects;

@SuppressWarnings("unused")
public class TransactionDebugInfo extends TransactionInfo {

    private final StateChanges stateChanges;

    public TransactionDebugInfo(Transaction tx,
                                ApplicationStatus applicationStatus,
                                int height,
                                StateChanges stateChanges) {
        super(tx, applicationStatus, height);

        this.stateChanges = stateChanges == null
                ? new StateChanges(null, null, null, null, null, null, null)
                : stateChanges;
    }

    public StateChanges stateChanges() {
        return stateChanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TransactionDebugInfo that = (TransactionDebugInfo) o;
        return Objects.equals(stateChanges, that.stateChanges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stateChanges);
    }

    @Override
    public String toString() {
        return "TransactionDebugInfo{" +
                "height=" + height() +
                ", applicationStatus=" + applicationStatus() +
                ", stateChanges=" + stateChanges +
                ", tx=" + tx() +
                '}';
    }

}
