package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

public interface IssueTransaction extends Transaction, Signable, WithId {
    static final byte ISSUE = 3;

    byte getChainId();

    String getName();

    String getDescription();

    long getQuantity();

    byte getDecimals();

    boolean isReissuable();

    String getScript();
}
