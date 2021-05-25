package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.CreateAliasTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class CreateAliasTransactionInfo extends TransactionInfo {

    public CreateAliasTransactionInfo(CreateAliasTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public CreateAliasTransaction tx() {
        return (CreateAliasTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "CreateAliasTransactionInfo{} " + super.toString();
    }

}
