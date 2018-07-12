package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class AliasTransaction extends Transaction {
    static final byte ALIAS         = 10;

    private PublicKeyAccount sender;
    private String alias;
    private byte chainId;
    private long fee;
    private long timestamp;

    public AliasTransaction(PublicKeyAccount sender, String alias, byte chainId, long fee, long timestamp) {
        this.sender = sender;
        this.alias = alias;
        this.chainId = chainId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public String getAlias() {
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
        buf.put(sender.getPublicKey()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", ALIAS);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("alias", alias);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
