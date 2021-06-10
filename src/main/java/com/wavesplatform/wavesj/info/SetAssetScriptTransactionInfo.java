package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.SetAssetScriptTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class SetAssetScriptTransactionInfo extends TransactionInfo {

    public SetAssetScriptTransactionInfo(SetAssetScriptTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public SetAssetScriptTransaction tx() {
        return (SetAssetScriptTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "SetAssetScriptTransactionInfo{} " + super.toString();
    }

}
