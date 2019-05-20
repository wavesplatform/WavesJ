package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.wavesplatform.wavesj.Asset.normalize;

public class BalanceDetails {
    private final String address;
    private final Long regular;
    private final Long available;
    private final Long generating;
    private final Long effective;


    @JsonCreator
    public BalanceDetails(
            @JsonProperty("address") String address,
            @JsonProperty("regular") Long regular,
            @JsonProperty("available") Long available,
            @JsonProperty("generating") Long generating,
            @JsonProperty("effective") Long effective
    ) {
        this.address = address;
        this.regular = regular;
        this.available = available;
        this.generating = generating;
        this.effective = effective;
    }


    public Long getRegular() {
        return regular;
    }

    public String getAddress() {
        return address;
    }


    public Long getGenerating() {
        return generating;
    }

    public Long getAvailable() {
        return available;
    }

    public Long getEffective() {
        return effective;
    }
}
