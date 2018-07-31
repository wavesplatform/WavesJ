package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wavesplatform.wavesj.Alias;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class AliasTransaction extends Transaction {
    public static final byte ALIAS = 10;

    private final PublicKeyAccount senderPublicKey;
    private final Alias alias;
    @JsonIgnore
    private final byte chainId;
    private final long fee;
    private final long timestamp;

    public AliasTransaction(PublicKeyAccount senderPublicKey, Alias alias, byte chainId, long fee, long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.alias = alias;
        this.chainId = chainId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public Alias getAlias() {
        return alias;
    }

    public byte getChainId() {
        return chainId;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(senderPublicKey.getPublicKey()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return ALIAS;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", ALIAS);
        data.put("id", getId());
        data.put("senderPublicKey", Base58.encode(senderPublicKey.getPublicKey()));
        data.put("alias", alias.toString());
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
