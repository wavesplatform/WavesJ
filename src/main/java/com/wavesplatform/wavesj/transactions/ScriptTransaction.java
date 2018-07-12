package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.Base64;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class ScriptTransaction extends Transaction {
    static final byte SET_SCRIPT = 13;

    private PublicKeyAccount sender;
    private String script;
    private byte chainId;
    private long fee;
    private long timestamp;

    public ScriptTransaction(PublicKeyAccount sender, String script, byte chainId, long fee, long timestamp) {
        this.sender = sender;
        this.script = script;
        this.chainId = chainId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
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
        buf.put(sender.getPublicKey());
        if (rawScript.length > 0) {
            buf.put((byte) 1).putShort((short) rawScript.length).put(rawScript);
        } else {
            buf.put((byte) 0);
        }
        buf.putLong(fee).putLong(timestamp);
        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", SET_SCRIPT);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("script", script);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
