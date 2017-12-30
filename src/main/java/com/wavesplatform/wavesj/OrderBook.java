package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBook {
    private List<Order> bids, asks;

    // needed for Jackson
    private OrderBook() {}

    public List<Order> getBids() {
        return Collections.unmodifiableList(bids);
    }

    public List<Order> getAsks() {
        return Collections.unmodifiableList(asks);
    }
}
