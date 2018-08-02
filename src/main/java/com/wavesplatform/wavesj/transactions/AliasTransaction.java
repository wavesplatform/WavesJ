package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Alias;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class AliasTransaction extends Transaction {
    public static final byte ALIAS = 10;

    private final PublicKeyAccount senderPublicKey;
    private final Alias alias;
    @JsonIgnore
    private final byte chainId;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public AliasTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                            @JsonProperty("alias") Alias alias,
                            @JsonProperty("chainId") byte chainId,
                            @JsonProperty("fee") long fee,
                            @JsonProperty("timestamp") long timestamp) {
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

}
