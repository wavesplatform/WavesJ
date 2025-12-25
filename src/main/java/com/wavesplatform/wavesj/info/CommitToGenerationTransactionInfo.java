package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.CommitToGenerationTransaction;
import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class CommitToGenerationTransactionInfo extends TransactionInfo{
    public CommitToGenerationTransactionInfo(Transaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public CommitToGenerationTransaction tx() {
        return (CommitToGenerationTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "CommitToGenerationTransaction{} " + super.toString();
    }
}
