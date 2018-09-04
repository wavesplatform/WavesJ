package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
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
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.data = data;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public DataTransaction(PrivateKeyAccount senderPublicKey,
                           Collection<DataEntry<?>> data,
                           long fee,
                           long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.data = data;
        this.fee = fee;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBytes()))));
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
        return Transaction.V1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataTransaction that = (DataTransaction) o;

        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        return getData() != null ? getData().equals(that.getData()) : that.getData() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
