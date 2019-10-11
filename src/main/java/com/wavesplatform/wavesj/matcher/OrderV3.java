package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class OrderV3 extends ObjectWithProofs implements Order {

    private final Type orderType;
    private final long amount;
    private final long price;
    private final long filled;
    private final long timestamp;
    private final Status status;
    private final AssetPair assetPair;
    private final long expiration;
    private final long matcherFee;
    private final String matcherFeeAssetId;
    private final PublicKeyAccount senderPublicKey;
    private final PublicKeyAccount matcherPublicKey;
    private final ByteString id;
    private final byte version;

    public OrderV3(
            PrivateKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey,
            Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee,
            String matcherFeeAssetId,
            byte version) {
        this.orderType = orderType;
        this.assetPair = assetPair;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;
        this.version = version;
        this.status = Status.ACCEPTED;
        this.filled = 0;
        this.expiration = expiration;
        this.matcherFee = matcherFee;
        this.matcherFeeAssetId = matcherFeeAssetId;
        this.senderPublicKey = senderPublicKey;
        this.matcherPublicKey = matcherKey;
        this.id = new ByteString(hash(getBodyBytes()));
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
    }

    public OrderV3(
            PublicKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey,
            Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee,
            String matcherFeeAssetId,
            byte version,
            List<ByteString> proofs) {
        setProofs(proofs);
        this.version = version;
        this.orderType = orderType;
        this.assetPair = assetPair;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;
        this.status = Status.ACCEPTED;
        this.filled = 0;
        this.expiration = expiration;
        this.matcherFee = matcherFee;
        this.matcherFeeAssetId = matcherFeeAssetId;
        this.senderPublicKey = senderPublicKey;
        this.matcherPublicKey = matcherKey;
        this.id = new ByteString(hash(getBodyBytes()));
    }

    @JsonCreator
    public OrderV3(
            @JsonProperty("id") String id,
            @JsonProperty("type") Type orderType,
            @JsonProperty("assetPair") AssetPair assetPair,
            @JsonProperty("amount") long amount,
            @JsonProperty("price") long price,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("filled") long filled,
            @JsonProperty("status") Status status,
            @JsonProperty("expiration") long expiration,
            @JsonProperty("matcherFee") long matcherFee,
            @JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
            @JsonProperty("matcherKey") PublicKeyAccount matcherKey,
            @JsonProperty("matcherFeeAssetId") String matcherFeeAssetId,
            @JsonProperty("version") byte version,
            @JsonProperty("proofs") List<ByteString> proofs) {
        this.version = version;
        setProofs(proofs);
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
        this.matcherFeeAssetId = matcherFeeAssetId;
        this.matcherPublicKey = matcherKey;
        if (id != null) {
            this.id = new ByteString(id);
        } else {
            this.id = null;
        }
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(Order.V3);
        buf.put(senderPublicKey.getPublicKey()).put(matcherPublicKey.getPublicKey());
        putAsset(buf, assetPair.getAmountAsset());
        putAsset(buf, assetPair.getPriceAsset());
        buf.put((byte) orderType.ordinal()).putLong(price).putLong(amount)
                .putLong(timestamp).putLong(expiration).putLong(matcherFee);
        putAsset(buf, matcherFeeAssetId);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf
                .put(getBodyBytes())
                .put((byte) 1) //proofs version
                .putShort((short) getProofs().size());
        for (ByteString p : getProofs()) {
            buf
                    .putShort((short) p.getBytes().length)
                    .put(p.getBytes());
        }
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public List<ByteString> getProofs() {
        return proofs;
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


    public String getMatcherFeeAssetId() {
        return matcherFeeAssetId;
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
        return Order.V3;
    }

    @JsonIgnore
    @Override
    public boolean isActive() {
        return status.isActive();
    }


    public OrderV3 withProof(int index, ByteString proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<ByteString> newProofs = new ArrayList<ByteString>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add(ByteString.EMPTY);
        }
        newProofs.set(index, proof);
        return new OrderV3(senderPublicKey, matcherPublicKey, orderType, assetPair, amount, price, timestamp, expiration, matcherFee, matcherFeeAssetId, Order.V3, newProofs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderV3 order = (OrderV3) o;

        return getId() != null ? getId().equals(order.getId()) : order.getId() == null;
    }

    @Override
    public ByteString getId() {
        return new ByteString(hash(getBodyBytes()));
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
