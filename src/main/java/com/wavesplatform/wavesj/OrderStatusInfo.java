package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.matcher.Order;

public class OrderStatusInfo {
    private long filled;
    private Order.Status status;

    @JsonCreator
    private OrderStatusInfo(
            @JsonProperty("filledAmount") long filled,
            @JsonProperty("status") Order.Status status) {

        this.status = status;
        this.filled = filled;
    }

    public long getFilled() {
        return filled;
    }

    public Order.Status getStatus() {
        return status;
    }
}
