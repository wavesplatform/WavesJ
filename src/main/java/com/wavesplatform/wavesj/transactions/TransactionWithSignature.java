package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.ApiJson;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionWithSignature<T extends Transaction> extends ApiJson {
    static final byte V1 = 1;

    protected final String signature;
    protected final T transaction;

    public TransactionWithSignature(T transaction, String signature) {
        this.signature = signature;
        this.transaction = transaction;
    }

    public TransactionWithSignature(T transaction, PrivateKeyAccount account) {
        this.signature = account.sign(transaction);
        this.transaction = transaction;
    }

    public String getSignature() {
        return signature;
    }

    public Map<String, Object> getData() {
        HashMap<String, Object> toJson = new HashMap<String, Object>(getData());
        toJson.put("id", transaction.getId());
        toJson.put("signature", signature);
        toJson.put("version", V1);
        return toJson;
    }
}
