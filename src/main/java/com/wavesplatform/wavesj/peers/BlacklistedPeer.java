package com.wavesplatform.wavesj.peers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BlacklistedPeer {

    private final String hostname;
    private final int timestamp;
    private final String reason;

    public BlacklistedPeer(
            @JsonProperty("hostname") String hostname,
            @JsonProperty("timestamp") int timestamp,
            @JsonProperty("reason") String reason
    ) {
        this.hostname = hostname;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    public String getHostname() {
        return hostname;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlacklistedPeer that = (BlacklistedPeer) o;
        return timestamp == that.timestamp && Objects.equals(hostname, that.hostname) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, timestamp, reason);
    }

    @Override
    public String toString() {
        return "BlacklistedPeer{" +
                "hostname='" + hostname + '\'' +
                ", timestamp=" + timestamp +
                ", reason='" + reason + '\'' +
                '}';
    }
}
