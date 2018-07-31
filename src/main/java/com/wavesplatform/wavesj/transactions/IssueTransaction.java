package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

//@JsonDeserialize(using = IssueTransaction.Deserializer.class)
public class IssueTransaction extends Transaction {
    public static final byte ISSUE = 3;

//    public static final TypeReference<IssueTransaction> TRANSACTION_TYPE = new TypeReference<IssueTransaction>() {};
//    public static final JavaType SIGNED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithSignature.class, IssueTransaction.class);
//    public static final JavaType PROOFED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithProofs.class, IssueTransaction.class);

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
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
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

    @Override
    public byte getType() {
        return ISSUE;
    }
}
