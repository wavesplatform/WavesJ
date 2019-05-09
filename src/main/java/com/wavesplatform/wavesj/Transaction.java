package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Transaction extends ApiJson, Signable {
    public static final byte V1 = 1;
    public static final byte V2 = 2;

    public long getFee();

    public long getTimestamp();

    /**
     * Transaction ID.
     */
    @JsonIgnore
    public ByteString getId();

    /**
     * Can be obtained ONLY during deserialization
     * @return transaction's height if available or 0
     */
    public int getHeight();

    public abstract PublicKeyAccount getSenderPublicKey();

    public abstract byte getType();

    public abstract byte getVersion();
}
