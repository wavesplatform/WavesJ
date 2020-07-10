package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;

import java.util.List;

/**
 * InvokeScriptTransaction with information about state changes
 */
public class InvokeScriptTransactionStCh extends InvokeScriptTransaction {

    private StateChanges stateChanges;

    /**
     * This class couldn't be used to post transaction to blockchain that why constructor is package-private
     */
    @JsonCreator
    InvokeScriptTransactionStCh(@JsonProperty("chainId") byte chainId,
                                @JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                @JsonProperty("dApp") String dApp,
                                @JsonProperty("call") FunctionCall call,
                                @JsonProperty("payment") List<Payment> payments,
                                @JsonProperty("fee") long fee,
                                @JsonProperty("feeAssetId") String feeAssetId,
                                @JsonProperty("timestamp") long timestamp,
                                @JsonProperty("proofs") List<ByteString> proofs,
                                @JsonProperty("stateChanges") StateChanges stateChanges) {
        super(chainId, senderPublicKey, dApp, call, payments, fee, feeAssetId, timestamp, proofs);
        this.stateChanges = stateChanges;
    }

    @Override
    public StateChanges getStateChanges() {
        return stateChanges;
    }

    @Override
    public boolean isStateChangesSupported() {
        return true;
    }
}
