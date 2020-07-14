package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Transaction extends ApiJson, Signable {
    byte V1 = 1;
    byte V2 = 2;

    long getFee();

    long getTimestamp();

    /**
     * Transaction ID.
     * @return transaction id in ByteString format
     */
    @JsonIgnore
    ByteString getId();

    /**
     * Can be obtained ONLY during deserialization
     * @return transaction's height if available or 0
     */
    int getHeight();

    /**
     * Can be obtained ONLY during deserialization
     * @return transaction's status if available or UNKNOWN
     */
    ApplicationStatus getApplicationStatus();

    PublicKeyAccount getSenderPublicKey();

    byte getType();

    byte getVersion();

    boolean isStateChangesSupported();
}
