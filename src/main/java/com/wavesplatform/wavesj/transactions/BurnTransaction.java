package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public interface BurnTransaction extends Transaction, Signable, WithId {
    static final byte BURN = 6;

    byte getChainId();

    String getAssetId();

    long getAmount();
}
