package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

public class IssueTransactionV1 extends TransactionWithSignature implements IssueTransaction {
    public static final byte ISSUE = 3;

    private final PublicKeyAccount senderPublicKey;
    private final String name;
    private final String description;
    private final long quantity;
    private final byte decimals;
    private final boolean reissuable;
    private final long fee;
    private final long timestamp;

    public IssueTransactionV1(PrivateKeyAccount senderPublicKey,
                              String name,
                              String description,
                              long quantity,
                              byte decimals,
                              boolean reissuable,
                              long fee,
                              long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = new ByteString(senderPublicKey.sign(getBytes()));
    }

    @JsonCreator
    public IssueTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("name") String name,
                              @JsonProperty("description") String description,
                              @JsonProperty("quantity") long quantity,
                              @JsonProperty("decimals") byte decimals,
                              @JsonProperty("reissuable") boolean reissuable,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("signature") ByteString signature) {
        this.senderPublicKey = senderPublicKey;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.reissuable = reissuable;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssueTransactionV1 that = (IssueTransactionV1) o;

        if (getQuantity() != that.getQuantity()) return false;
        if (getDecimals() != that.getDecimals()) return false;
        if (isReissuable() != that.isReissuable()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (int) (getQuantity() ^ (getQuantity() >>> 32));
        result = 31 * result + (int) getDecimals();
        result = 31 * result + (isReissuable() ? 1 : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
