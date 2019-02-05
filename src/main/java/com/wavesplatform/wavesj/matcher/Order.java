package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wavesplatform.wavesj.*;

import java.util.List;

public interface Order  extends Signable, ApiJson {
    byte V1 = 1;
    byte V2 = 2;

    enum Type {
        BUY, SELL;

        @JsonValue
        public String toJson() {
            return toString().toLowerCase();
        }

        @JsonCreator
        public static Order.Type fromString(String json) {
            return json == null ? null : Order.Type.valueOf(json.toUpperCase());
        }
    }

    enum Status {
        ACCEPTED, FILLED, PARTIALLY_FILLED, CANCELED, NOT_FOUND;

        @JsonCreator
        public static Order.Status fromString(String json) {
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

    /**
     * Transaction ID.
     * @return transaction id in ByteString format
     */
    @JsonIgnore
    ByteString getId();

    Order.Type getOrderType();

    long getAmount();

    long getPrice();

    long getFilled();

    long getTimestamp();

    Order.Status getStatus();

    AssetPair getAssetPair();

    long getExpiration();

    long getMatcherFee() ;

    PublicKeyAccount getMatcherPublicKey();

    byte getVersion();

    List<ByteString> getProofs();

    boolean isActive();
}
