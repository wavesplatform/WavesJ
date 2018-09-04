package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class Order extends ObjectWithSignature implements ApiJson {
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

    private final Order.Type orderType;
    private final long amount;
    private final long price;
    private final long filled;
    private final long timestamp;
    private final Order.Status status;
    private final AssetPair assetPair;
    private final long expiration;
    private final long matcherFee;
    private final PublicKeyAccount senderPublicKey;
    private final PublicKeyAccount matcherPublicKey;
    private final ByteString id;

    public Order(
            Order.Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee,
            PrivateKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey) {
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
        this.signature = new ByteString(senderPublicKey.sign(getBytes()));
    }

    public Order(
            Order.Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long expiration,
            long matcherFee,
            PublicKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey,
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
        this.id = new ByteString(hash(getBytes()));
    }

    @JsonCreator
    public Order(
            @JsonProperty("id") String id,
            @JsonProperty("type") Order.Type orderType,
            @JsonProperty("assetPair") AssetPair assetPair,
            @JsonProperty("amount") long amount,
            @JsonProperty("price") long price,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("filled") long filled,
            @JsonProperty("status") Order.Status status,
            @JsonProperty("expiration") long expiration,
            @JsonProperty("matcherFee") long matcherFee,
            @JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
            @JsonProperty("matcherKey") PublicKeyAccount matcherKey,
            @JsonProperty("signature") ByteString signature) {
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
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(senderPublicKey.getPublicKey()).put(matcherPublicKey.getPublicKey());
        putAsset(buf, assetPair.getAmountAsset());
        putAsset(buf, assetPair.getPriceAsset());
        buf.put((byte) orderType.ordinal()).putLong(price).putLong(amount)
                .putLong(timestamp).putLong(expiration).putLong(matcherFee);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    public Order.Type getOrderType() {
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

    public Order.Status getStatus() {
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

    @JsonIgnore
    public boolean isActive() {
        return status.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return getId() != null ? getId().equals(order.getId()) : order.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
