package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.matcher.AmountAndPrice;

import java.util.Collections;
import java.util.List;

public class OrderBook {
    private List<AmountAndPrice> bids, asks;

    @JsonCreator
    private OrderBook(
            @JsonProperty("bids") List<AmountAndPrice> bids,
            @JsonProperty("asks") List<AmountAndPrice> asks) {
        this.bids = Collections.unmodifiableList(bids);
        this.asks = Collections.unmodifiableList(asks);
    }

    public List<AmountAndPrice> getBids() {
        return bids;
    }

    public List<AmountAndPrice> getAsks() {
        return asks;
    }
}
