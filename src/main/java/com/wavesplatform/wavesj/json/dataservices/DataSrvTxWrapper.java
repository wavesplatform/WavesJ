package com.wavesplatform.wavesj.json.dataservices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Transaction;

import java.io.Serializable;

public class DataSrvTxWrapper<T extends Transaction> implements Serializable {

    private static final long serialVersionUID = 6081164439692182061L;

    @JsonProperty("data")
    private T tx;

    public T getTx() {
        return tx;
    }

    public void setTx(T tx) {
        this.tx = tx;
    }
}
