package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

public class IssueTransaction extends Transaction {
    static final byte ISSUE = 3;

    private final PublicKeyAccount sender;
    private final byte chainId;
    private final String name;
    private final String description;
    private final long quantity;
    private final byte decimals;
    private final boolean reissuable;
    private final String script;
    private final long fee;
    private final long timestamp;

    public IssueTransaction(PublicKeyAccount sender, byte chainId, String name, String description, long quantity, byte decimals, boolean reissuable, String script, long fee, long timestamp) {
        this.sender = sender;
        this.chainId = chainId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.script = script;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public byte getChainId() {
        return chainId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getQuantity() {
        return quantity;
    }

    public byte getDecimals() {
        return decimals;
    }

    public boolean isReissuable() {
        return reissuable;
    }

    public String getScript() {
        return script;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(10 * KBYTE);
        buf.put(sender.getPublicKey());
        putString(buf, name);
        putString(buf, description);
        buf.putLong(quantity)
                .put(decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee)
                .putLong(timestamp);
        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", ISSUE);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("name", name);
        data.put("description", description);
        data.put("quantity", quantity);
        data.put("decimals", decimals);
        data.put("reissuable", reissuable);
        data.put("script", script);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
