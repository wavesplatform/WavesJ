package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class AssetDistribution {
    public final String lastItem;
    public final Boolean hasNext;
    public final Map<String, Long> items;

    @JsonCreator
    public AssetDistribution(
            @JsonProperty("lastItem") String lastItem,
            @JsonProperty("hasItems") Boolean hasNext,
            @JsonProperty("items") Map<String, Long> items) {
        this.lastItem = lastItem;
        this.hasNext = hasNext;
        this.items = items;
    }

    private AssetDistribution(String lastItem, Boolean hasNext, Map<String, Long> items, Object unused) {
        this.lastItem = lastItem;
        this.hasNext = hasNext;
        this.items = items;
    }

    public Map<String, Long> getItems() {
        return items;
    }

    public String getLastItem() {
        return lastItem;
    }

    public Boolean isHasNext() {
        return hasNext;
    }
}
