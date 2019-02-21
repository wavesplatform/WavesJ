package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;


public class OrderV1 extends ObjectWithSignature implements Order {


    private final Type orderType;
    private final long amount;
    private final long price;
    private final long filled;
    private final long timestamp;
    private final Status status;
    private final AssetPair assetPair;
    private final long expiration;
    private final long matcherFee;
    private final PublicKeyAccount senderPublicKey;
    private final PublicKeyAccount matcherPublicKey;
    private final ByteString id;
    private final List<ByteString> proofs;

    public OrderV1(
            PrivateKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey,
            Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee) {
        this.orderType = orderType;
        this.assetPair = assetPair;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;
        this.status = Status.ACCEPTED;
        this.filled = 0;
        this.expiration = expiration;
        this.matcherFee = matcherFee;
        this.senderPublicKey = senderPublicKey;
        this.matcherPublicKey = matcherKey;
        this.id = new ByteString(hash(getBodyBytes()));
        this.signature = new ByteString(senderPublicKey.sign(getBodyBytes()));
        this.proofs = setProofs(this.signature);
    }

    private List<ByteString> setProofs(ByteString signature) {
        List<ByteString> p = new ArrayList<ByteString>();
        p.add(signature);
        return p;
    }

    public OrderV1(
            PublicKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey,
            Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee,
            ByteString signature) {
        this.orderType = orderType;
        this.assetPair = assetPair;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;

        this.status = Status.ACCEPTED;
        this.filled = 0;
        this.expiration = expiration;
        this.matcherFee = matcherFee;
        this.senderPublicKey = senderPublicKey;
        this.matcherPublicKey = matcherKey;
        this.signature = signature;
        this.id = new ByteString(hash(getBodyBytes()));
        this.proofs = setProofs(signature);
    }

    @JsonCreator
    public OrderV1(
            @JsonProperty("id") String id,
            @JsonProperty("type") Type orderType,
            @JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
            @JsonProperty("matcherKey") PublicKeyAccount matcherKey,
            @JsonProperty("assetPair") AssetPair assetPair,
            @JsonProperty("amount") long amount,
            @JsonProperty("price") long price,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("filled") long filled,
            @JsonProperty("status") Status status,
            @JsonProperty("expiration") long expiration,
            @JsonProperty("matcherFee") long matcherFee,
            @JsonProperty("signature") ByteString signature,
            @JsonProperty("proofs") List<ByteString> proofs) {
        this.orderType = orderType;
        this.assetPair = assetPair;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;
        this.status = status;
        this.filled = filled;
        this.expiration = expiration;
        this.matcherFee = matcherFee;
        this.senderPublicKey = senderPublicKey;
        this.matcherPublicKey = matcherKey;
        this.signature = signature;
        if (id != null) {
            this.id = new ByteString(id);
        } else {
            this.id = null;
        }
        this.proofs = proofs;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(senderPublicKey.getPublicKey()).put(matcherPublicKey.getPublicKey());
        putAsset(buf, assetPair.getAmountAsset());
        putAsset(buf, assetPair.getPriceAsset());
        buf.put((byte) orderType.ordinal()).putLong(price).putLong(amount)
                .putLong(timestamp).putLong(expiration).putLong(matcherFee);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf
                .put(getBodyBytes())
                .put(getSignature().getBytes());
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public ByteString getId() {
        return new ByteString(hash(getBodyBytes()));
    }

    @Override
    public Type getOrderType() {
        return orderType;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public long getFilled() {
        return filled;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public AssetPair getAssetPair() {
        return assetPair;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public long getMatcherFee() {
        return matcherFee;
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public PublicKeyAccount getMatcherPublicKey() {
        return matcherPublicKey;
    }

    @Override
    public byte getVersion() {
        return Order.V1;
    }

    @Override
    public List<ByteString> getProofs() {
        List<ByteString> p = new ArrayList<ByteString>();
        p.add(getSignature());
        return p;
    }


    @JsonIgnore
    @Override
    public boolean isActive() {
        return getStatus().isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderV1 order = (OrderV1) o;

        return getId() != null ? getId().equals(order.getId()) : order.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
