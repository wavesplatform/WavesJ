package com.wavesplatform.wavesj;

import im.mak.waves.transactions.GenesisTransaction;
import im.mak.waves.transactions.Transaction;

import java.util.Objects;

public class TransactionInfo {

    private final int height;
    private final ApplicationStatus applicationStatus;
    private final Transaction tx;

    public TransactionInfo(Transaction tx,
                           ApplicationStatus applicationStatus,
                           int height) {
        this.tx = Common.notNull(tx, "Transaction");
        this.height = height == 0 && tx.type() == GenesisTransaction.TYPE ? 1 : height;
        this.applicationStatus = applicationStatus == null ? ApplicationStatus.SUCCEEDED : applicationStatus;
    }

    public int height() {
        return height;
    }

    public ApplicationStatus applicationStatus() {
        return applicationStatus;
    }

    public Transaction tx() {
        return tx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionInfo that = (TransactionInfo) o;
        return height == that.height &&
                applicationStatus == that.applicationStatus &&
                Objects.equals(tx, that.tx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, applicationStatus, tx);
    }

    @Override
    public String toString() {
        return "TransactionInfo{" +
                "height=" + height +
                ", applicationStatus=" + applicationStatus +
                ", tx=" + tx +
                '}';
    }

}
