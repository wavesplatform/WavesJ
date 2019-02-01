package com.wavesplatform.wavesj.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wavesplatform.wavesj.ApiJson;
import com.wavesplatform.wavesj.Signable;

public interface Order  extends Signable, ApiJson {

    enum Type {
        BUY, SELL;

        @JsonValue
        public String toJson() {
            return toString().toLowerCase();
        }

        @JsonCreator
        public static OrderV2.Type fromString(String json) {
            return json == null ? null : OrderV2.Type.valueOf(json.toUpperCase());
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
}
