package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.ObjectWithSignature;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.Collection;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class DataTransaction extends Transaction {
    public static final byte DATA = 12;

    private final PublicKeyAccount senderPublicKey;
    private final Collection<DataEntry<?>> data;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public DataTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                           @JsonProperty("data") Collection<DataEntry<?>> data,
                           @JsonProperty("fee") long fee,
                           @JsonProperty("timestamp") long timestamp) {
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
        buf.put(DATA).put(ObjectWithSignature.V1).put(senderPublicKey.getPublicKey());
        buf.putShort((short) data.size());
        for (DataEntry<?> e : data) {
            e.write(buf);
        }
        buf.putLong(timestamp).putLong(fee);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
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
}
