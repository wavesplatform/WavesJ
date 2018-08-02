package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wavesplatform.wavesj.ApiJson;
import com.wavesplatform.wavesj.AssetPair;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Signable;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class Order extends ApiJson implements Signable {
    public String getId() {
        return hash(getBytes());
    }

    public enum Type {
        BUY, SELL;

        @JsonValue
        public String toJson() {
            return toString().toLowerCase();
        }

        @JsonCreator
        static Type fromString(String json) {
            return json == null ? null : Type.valueOf(json.toUpperCase());
        }
    }

    public enum Status {
        ACCEPTED, FILLED, PARTIALLY_FILLED, CANCELED, NOT_FOUND;

        @JsonCreator
        public static Status fromString(String json) {
            if (json == null) return null;
            json = json.intern();
            if (json == "Accepted" || json == "OrderAccepted") {
                return ACCEPTED;
            } else if (json == "Filled") {
                return FILLED;
            } else if (json == "PartiallyFilled") {
                return PARTIALLY_FILLED;
            } else if (json == "Cancelled") {
                return CANCELED;
            } else if (json == "NotFound") {
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

    @JsonCreator
    public Order(
            Order.Type orderType,
            AssetPair assetPair,
            long amount,
            long price,
            long timestamp,
            long filled,
            Order.Status status,
            long expiration,
            long matcherFee,
            PublicKeyAccount senderPublicKey,
            PublicKeyAccount matcherKey) {
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
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(senderPublicKey.getPublicKey()).put(matcherPublicKey.getPublicKey());
        putAsset(buf, assetPair.amountAsset);
        putAsset(buf, assetPair.priceAsset);
        buf.put((byte) orderType.ordinal()).putLong(price).putLong(amount)
                .putLong(timestamp).putLong(expiration).putLong(matcherFee);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
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
}
