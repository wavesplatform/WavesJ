package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public interface LeaseCancelTransaction extends Transaction, Signable, WithId {
    public static final byte LEASE_CANCEL = 9;

    public byte getChainId();

    public String getLeaseId();
}
