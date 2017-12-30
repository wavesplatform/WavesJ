package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    public enum Type {
        BUY("buy"),
        SELL("sell");

        @JsonValue
        final String json;

        Type(String json) {
            this.json = json;
        }

        @JsonCreator
        public static Type fromString(String json) {
            return json == null ? null : Type.valueOf(json.toUpperCase());
        }
    }

    public String id;
    public Type type;
    public long amount;
    public long price;
    public long filled;
    public long timestamp;
    public String status;

    // needed for Jackson
    private Order() {}
}
