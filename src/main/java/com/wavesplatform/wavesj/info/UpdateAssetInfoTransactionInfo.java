package com.wavesplatform.wavesj.info;

import com.wavesplatform.transactions.UpdateAssetInfoTransaction;
import com.wavesplatform.wavesj.ApplicationStatus;

public class UpdateAssetInfoTransactionInfo extends TransactionInfo {

    public UpdateAssetInfoTransactionInfo(UpdateAssetInfoTransaction tx, ApplicationStatus applicationStatus, int height) {
        super(tx, applicationStatus, height);
    }

    public UpdateAssetInfoTransaction tx() {
        return (UpdateAssetInfoTransaction) super.tx();
    }

    @Override
    public String toString() {
        return "UpdateAssetInfoTransactionInfo{} " + super.toString();
    }

}
