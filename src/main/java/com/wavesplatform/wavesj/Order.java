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

    private String id;
    private Type type;
    private long amount;
    private long price;
    private long filled;
    private long timestamp;
    private String status;

    // needed for Jackson
    private Order() {}

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
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

    public String getStatus() {
        return status;
    }
}
