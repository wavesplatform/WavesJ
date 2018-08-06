package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public interface ReissueTransaction extends Transaction, Signable, WithId {
    public static final byte REISSUE = 5;

    byte getChainId();

    String getAssetId();

    long getQuantity();

    boolean isReissuable();
}
