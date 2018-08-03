package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class DataTransaction extends TransactionWithProofs {
    public static final byte DATA = 12;

    private final PublicKeyAccount senderPublicKey;
    private final Collection<DataEntry<?>> data;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public DataTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                           @JsonProperty("data") Collection<DataEntry<?>> data,
                           @JsonProperty("fee") long fee,
                           @JsonProperty("timestamp") long timestamp,
                           @JsonProperty("proofs") List<ByteString> proofs) {
        super(proofs);
        this.senderPublicKey = senderPublicKey;
        this.data = data;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public DataTransaction(PrivateKeyAccount senderPublicKey,
                           Collection<DataEntry<?>> data,
                           long fee,
                           long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.data = data;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public byte[] getBytes() {
        int datalen = KBYTE;
        for (DataEntry<?> e : data) {
            datalen += e.size();
        }

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(DATA).put(TransactionWithSignature.V1).put(senderPublicKey.getPublicKey());
        buf.putShort((short) data.size());
        for (DataEntry<?> e : data) {
            e.write(buf);
        }
        buf.putLong(timestamp).putLong(fee);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    public Collection<DataEntry<?>> getData() {
        return data;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte getType() {
        return DATA;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
