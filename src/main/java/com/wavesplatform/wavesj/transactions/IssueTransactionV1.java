package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

public class IssueTransactionV1 extends TransactionWithSignature implements IssueTransaction {
    public static final byte ISSUE = 3;

    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String name;
    private final String description;
    private final long quantity;
    private final byte decimals;
    private final boolean reissuable;
    private final long fee;
    private final long timestamp;

    public IssueTransactionV1(PrivateKeyAccount senderPublicKey,
                              byte chainId,
                              String name,
                              String description,
                              long quantity,
                              byte decimals,
                              boolean reissuable,
                              long fee,
                              long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @JsonCreator
    public IssueTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("chainId") byte chainId,
                              @JsonProperty("name") String name,
                              @JsonProperty("description") String description,
                              @JsonProperty("quantity") long quantity,
                              @JsonProperty("decimals") byte decimals,
                              @JsonProperty("reissuable") boolean reissuable,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("signature") ByteString signature) {
        super(signature);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
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

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(10 * KBYTE);
        buf.put(IssueTransaction.ISSUE);
        buf.put(senderPublicKey.getPublicKey());
        putString(buf, name);
        putString(buf, description);
        buf.putLong(quantity)
                .put(decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee)
                .putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return ISSUE;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }
}
