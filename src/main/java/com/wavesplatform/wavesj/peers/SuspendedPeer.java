package com.wavesplatform.wavesj.peers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SuspendedPeer {

    private final String hostname;
    private final long timestamp;

    public SuspendedPeer(
            @JsonProperty("hostname") String hostname,
            @JsonProperty("timestamp") long timestamp
    ) {
        this.hostname = hostname;
        this.timestamp = timestamp;
    }

    public String getHostname() {
        return hostname;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuspendedPeer that = (SuspendedPeer) o;
        return timestamp == that.timestamp && Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, timestamp);
    }

    @Override
    public String toString() {
        return "SuspendedPeer{" +
                "hostname='" + hostname + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
