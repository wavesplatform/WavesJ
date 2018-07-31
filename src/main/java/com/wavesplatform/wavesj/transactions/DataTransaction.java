package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

//@JsonDeserialize(using = DataTransaction.Deserializer.class)
public class DataTransaction extends Transaction {
    public static final byte DATA = 12;

//    public static final TypeReference<DataTransaction> TRANSACTION_TYPE = new TypeReference<DataTransaction>() {};
//    public static final JavaType SIGNED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithSignature.class, DataTransaction.class);
//    public static final JavaType PROOFED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithProofs.class, DataTransaction.class);

    private final PublicKeyAccount sender;
    private final Collection<DataEntry<?>> data;
    private final long fee;
    private final long timestamp;

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
        int datalen = KBYTE;
        for (DataEntry<?> e: data) {
            datalen += e.size();
        }

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(DATA).put(ObjectWithSignature.V1).put(sender.getPublicKey());
        buf.putShort((short) data.size());
        for (DataEntry<?> e: data) {
            e.write(buf);
        }
        buf.putLong(timestamp).putLong(fee);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", DATA);
        data.put("id", getId());
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("data", data);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
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

    @Override
    public byte getType() {
        return DATA;
    }
}
