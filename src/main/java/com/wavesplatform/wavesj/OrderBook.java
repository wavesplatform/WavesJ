package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBook {
    public List<Order> bids, asks;

    // needed for Jackson
    private OrderBook() {}
}
