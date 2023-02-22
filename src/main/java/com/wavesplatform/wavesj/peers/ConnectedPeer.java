package com.wavesplatform.wavesj.peers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ConnectedPeer {

    private final String address;
    private final String declaredAddress;
    private final String peerName;
    private final long peerNonce;
    private final String applicationName;
    private final String applicationVersion;

    public ConnectedPeer(
            @JsonProperty("address") String address,
            @JsonProperty("declaredAddress") String declaredAddress,
            @JsonProperty("peerName") String peerName,
            @JsonProperty("peerNonce") long peerNonce,
            @JsonProperty("applicationName") String applicationName,
            @JsonProperty("applicationVersion") String applicationVersion
    ) {
        this.address = address;
        this.declaredAddress = declaredAddress;
        this.peerName = peerName;
        this.peerNonce = peerNonce;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    public String getAddress() {
        return address;
    }

    public String getDeclaredAddress() {
        return declaredAddress;
    }

    public String getPeerName() {
        return peerName;
    }

    public long getPeerNonce() {
        return peerNonce;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectedPeer that = (ConnectedPeer) o;
        return peerNonce == that.peerNonce && Objects.equals(address, that.address) && Objects.equals(declaredAddress, that.declaredAddress) && Objects.equals(peerName, that.peerName) && Objects.equals(applicationName, that.applicationName) && Objects.equals(applicationVersion, that.applicationVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, declaredAddress, peerName, peerNonce, applicationName, applicationVersion);
    }

    @Override
    public String toString() {
        return "ConnectedPeer{" +
                "address='" + address + '\'' +
                ", declaredAddress='" + declaredAddress + '\'' +
                ", peerName='" + peerName + '\'' +
                ", peerNonce=" + peerNonce +
                ", applicationName='" + applicationName + '\'' +
                ", applicationVersion='" + applicationVersion + '\'' +
                '}';
    }
}

//{
//      "address": "/185.32.14.61:56934",
//      "declaredAddress": "N/A",
//      "peerName": "Node-64119",
//      "peerNonce": 64119,
//      "applicationName": "wavesW",
//      "applicationVersion": "1.4.10"
//    }
