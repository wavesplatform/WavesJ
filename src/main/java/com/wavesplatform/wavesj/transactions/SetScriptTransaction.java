package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Base64;
import com.wavesplatform.wavesj.ByteArraysUtils;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class SetScriptTransaction extends TransactionWithBytesHashId {
    public static final byte SET_SCRIPT = 13;

    private PublicKeyAccount senderPublicKey;
    private String script;
    private byte chainId;
    private long fee;
    private long timestamp;

    @JsonCreator
    public SetScriptTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                @JsonProperty("script") String script,
                                @JsonProperty("chainId") byte chainId,
                                @JsonProperty("fee") long fee,
                                @JsonProperty("timestamp") long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.script = script;
        this.chainId = chainId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public String getScript() {
        return script;
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
        byte[] rawScript = script == null ? new byte[0] : Base64.decode(script);
        ByteBuffer buf = ByteBuffer.allocate(KBYTE + rawScript.length);
        buf.put(senderPublicKey.getPublicKey());
        if (rawScript.length > 0) {
            buf.put((byte) 1).putShort((short) rawScript.length).put(rawScript);
        } else {
            buf.put((byte) 0);
        }
        buf.putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return SET_SCRIPT;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }
}
