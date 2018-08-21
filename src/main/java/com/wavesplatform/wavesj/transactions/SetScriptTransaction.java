package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class SetScriptTransaction extends TransactionWithProofs {
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
                                @JsonProperty("timestamp") long timestamp,
                                @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.script = script;
        this.chainId = chainId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public SetScriptTransaction(PrivateKeyAccount senderPublicKey,
                                String script,
                                byte chainId,
                                long fee,
                                long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.script = script;
        this.chainId = chainId;
        this.fee = fee;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBytes()))));
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
        buf.put(SetScriptTransaction.SET_SCRIPT).put(Transaction.V1).put(chainId);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SetScriptTransaction that = (SetScriptTransaction) o;

        if (getChainId() != that.getChainId()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        return getScript() != null ? getScript().equals(that.getScript()) : that.getScript() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getScript() != null ? getScript().hashCode() : 0);
        result = 31 * result + (int) getChainId();
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
