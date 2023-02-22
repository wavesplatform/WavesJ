package com.wavesplatform.wavesj.peers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Peer {

    private final String address;
    private final long lastSeen;

    public Peer(
            @JsonProperty("address") String address,
            @JsonProperty("lastSeen") long lastSeen
    ) {
        this.address = address;
        this.lastSeen = lastSeen;
    }

    public String getAddress() {
        return address;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return lastSeen == peer.lastSeen && Objects.equals(address, peer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, lastSeen);
    }

    @Override
    public String toString() {
        return "Peer{" +
                "address='" + address + '\'' +
                ", lastSeen=" + lastSeen +
                '}';
    }
}
