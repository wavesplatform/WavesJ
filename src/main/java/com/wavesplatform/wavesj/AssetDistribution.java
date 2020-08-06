package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import im.mak.waves.transactions.account.Address;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class AssetDistribution {

    private final Map<Address, Long> items;
    private final Address lastItem;
    private final boolean hasNext;

    @JsonCreator
    public AssetDistribution(@JsonProperty("items") Map<Address, Long> items,
                             @JsonProperty("lastItem") Address lastItem,
                             @JsonProperty("hasNext") boolean hasNext) {
        this.items = Common.notNull(items, "Items");
        this.lastItem = Common.notNull(lastItem, "LastItem");
        this.hasNext = hasNext;
    }

    public Map<Address, Long> items() {
        return items;
    }

    public Address lastItem() {
        return lastItem;
    }

    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetDistribution that = (AssetDistribution) o;
        return hasNext == that.hasNext &&
                items.equals(that.items) &&
                lastItem.equals(that.lastItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, lastItem, hasNext);
    }

    @Override
    public String toString() {
        return "Distribution{" +
                "items=" + items +
                ", lastItem=" + lastItem.toString() +
                ", hasNext=" + hasNext +
                '}';
    }

}
