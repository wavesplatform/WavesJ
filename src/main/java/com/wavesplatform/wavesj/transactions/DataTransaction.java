package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataTransaction extends Transaction {

    static final byte DATA          = 12;

    private PublicKeyAccount sender;
    private Collection<DataEntry<?>> data;
    private long fee;
    private long timestamp;

    public DataTransaction(PublicKeyAccount sender, Collection<DataEntry<?>> data, long fee, long timestamp) {
        this.sender = sender;
        this.data = data;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", DATA);
        data.put(        "senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put(        "data", data);
        data.put(        "fee", fee);
        data.put(        "timestamp", timestamp);
        return data;
    }

    public Collection<DataEntry<?>> getTransactionData() {
        return data;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
