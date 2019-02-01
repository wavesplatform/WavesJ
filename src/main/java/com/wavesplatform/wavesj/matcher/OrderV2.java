package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class OrderV2 extends ObjectWithProofs implements ApiJson {
    public ByteString getId() {
        return id;
    }

    public enum Type {
        BUY, SELL;

        @JsonValue
        public String toJson() {
            return toString().toLowerCase();
        }

        @JsonCreator
        public static Type fromString(String json) {
            return json == null ? null : Type.valueOf(json.toUpperCase());
        }
    }

    public enum Status {
        ACCEPTED, FILLED, PARTIALLY_FILLED, CANCELED, NOT_FOUND;

        @JsonCreator
        public static Status fromString(String json) {
            if (json == null) return null;
            String upper = json.toUpperCase();
            if (upper.equals("ACCEPTED") || upper.equals("ORDERACCEPTED")) {
                return ACCEPTED;
            } else if (upper.equals("FILLED")) {
                return FILLED;
            } else if (upper.equals("PARTIALLYFILLED")) {
                return PARTIALLY_FILLED;
            } else if (upper.equals("CANCELLED")) {
                return CANCELED;
            } else if (upper.equals("NOTFOUND")) {
                return NOT_FOUND;
            } else {
                throw new IllegalArgumentException("Bad status value: " + json);
            }
        }

        public boolean isActive() {
            return this == ACCEPTED || this == PARTIALLY_FILLED;
        }
    }

    private final OrderV2.Type orderType;
    private final long amount;
    private final long price;
    private final long filled;
    private final long timestamp;
    private final OrderV2.Status status;
    private final AssetPair assetPair;
    private final long expiration;
    private final long matcherFee;
    private final PublicKeyAccount senderPublicKey;
    private final PublicKeyAccount matcherPublicKey;
    private final ByteString id;
    private final byte version;

    public OrderV2(
            Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee,
            PrivateKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey, byte version) {
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
        this.senderPublicKey = senderPublicKey;
        this.matcherPublicKey = matcherKey;
        this.id = new ByteString(hash(getBytes()));
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBytes()))));
    }

    public OrderV2(
            Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee,
            PublicKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey,
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
        this.senderPublicKey = senderPublicKey;
        this.matcherPublicKey = matcherKey;
        this.id = new ByteString(hash(getBytes()));
    }

    @JsonCreator
    public OrderV2(
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
        this.matcherPublicKey = matcherKey;
        if (id != null) {
            this.id = new ByteString(id);
        } else {
            this.id = null;
        }
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(Transaction.V2);
        buf.put(senderPublicKey.getPublicKey()).put(matcherPublicKey.getPublicKey());
        putAsset(buf, assetPair.getAmountAsset());
        putAsset(buf, assetPair.getPriceAsset());
        buf.put((byte) orderType.ordinal()).putLong(price).putLong(amount)
                .putLong(timestamp).putLong(expiration).putLong(matcherFee);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    public OrderV2.Type getOrderType() {
        return orderType;
    }

    public long getAmount() {
        return amount;
    }

    public long getPrice() {
        return price;
    }

    public long getFilled() {
        return filled;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public OrderV2.Status getStatus() {
        return status;
    }

    public AssetPair getAssetPair() {
        return assetPair;
    }

    public long getExpiration() {
        return expiration;
    }

    public long getMatcherFee() {
        return matcherFee;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public PublicKeyAccount getMatcherPublicKey() {
        return matcherPublicKey;
    }

    public byte getVersion() {
        return version;
    }

    @JsonIgnore
    public boolean isActive() {
        return status.isActive();
    }


    public OrderV2 withProof(int index, ByteString proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<ByteString> newProofs = new ArrayList<ByteString>(proofs);
        for (int i = newProofs.size(); i <= index; i++) {
            newProofs.add(ByteString.EMPTY);
        }
        newProofs.set(index, proof);
        return new OrderV2(orderType, assetPair, amount, price, timestamp, expiration, matcherFee, senderPublicKey, matcherPublicKey, (byte) 2, newProofs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderV2 order = (OrderV2) o;

        return getId() != null ? getId().equals(order.getId()) : order.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
