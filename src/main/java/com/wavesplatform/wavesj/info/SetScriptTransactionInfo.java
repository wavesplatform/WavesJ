package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.SetScriptTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class SetScriptTransactionInfo extends TransactionInfo {

    public SetScriptTransactionInfo(SetScriptTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public SetScriptTransaction tx() {
        return (SetScriptTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "SetScriptTransactionInfo{} " + super.toString();
    }

}
