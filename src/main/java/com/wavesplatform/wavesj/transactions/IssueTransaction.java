package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

public class IssueTransaction extends Transaction {
    public static final byte ISSUE = 3;

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

    @JsonCreator
    public IssueTransaction(@JsonProperty("sender") PublicKeyAccount sender,
                            @JsonProperty("chainId") byte chainId,
                            @JsonProperty("name") String name,
                            @JsonProperty("description") String description,
                            @JsonProperty("quantity") long quantity,
                            @JsonProperty("decimals") byte decimals,
                            @JsonProperty("reissuable") boolean reissuable,
                            @JsonProperty("script") String script,
                            @JsonProperty("fee") long fee,
                            @JsonProperty("timestamp") long timestamp) {
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
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return ISSUE;
    }
}
