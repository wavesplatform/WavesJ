package com.wavesplatform.wavesj;

import java.util.Collections;
import java.util.List;

public class OrderBook {
    public List<Order> bids, asks;

    OrderBook(List<Order> bids, List<Order> asks) {
        this.bids = Collections.unmodifiableList(bids);
        this.asks = Collections.unmodifiableList(asks);
    }
}
