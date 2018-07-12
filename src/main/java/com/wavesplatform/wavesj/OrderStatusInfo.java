package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.matcher.Order;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderStatusInfo {


    public final long filled;
    public final Order.Status status;

    @JsonCreator
    private OrderStatusInfo(
            @JsonProperty("filled") long filled,
            @JsonProperty("status") Order.Status status) {

        this.status = status;
        this.filled = filled;
    }
}
