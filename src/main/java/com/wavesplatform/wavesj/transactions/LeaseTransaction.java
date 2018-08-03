package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putRecipient;

public interface LeaseTransaction extends Transaction, Signable, WithId {
    static final byte LEASE = 8;

    String getRecipient();

    long getAmount();

    long getFee();

    long getTimestamp();
}
