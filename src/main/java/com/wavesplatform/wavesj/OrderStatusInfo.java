package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.matcher.OrderV1;

public class OrderStatusInfo {
    private long filled;
    private OrderV1.Status status;

    @JsonCreator
    private OrderStatusInfo(
            @JsonProperty("filledAmount") long filled,
            @JsonProperty("status") OrderV1.Status status) {

        this.status = status;
        this.filled = filled;
    }

    public long getFilled() {
        return filled;
    }

    public OrderV1.Status getStatus() {
        return status;
    }
}
