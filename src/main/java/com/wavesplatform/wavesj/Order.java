package com.wavesplatform.wavesj;

public class Order {
    public enum Type {
        BUY("buy"),
        SELL("sell");

        final String json;

        Type(String json) {
            this.json = json;
        }
    }

    public final long price;
    public final long amount;

    Order(long price, long amount) {
        this.price = price;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Order[price=" + price + ", amount=" + amount + ']';
    }
}
