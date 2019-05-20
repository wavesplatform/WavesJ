package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;

public interface IssueTransaction extends Transaction, Signable, WithId {
    byte ISSUE = 3;

    String getName();

    String getDescription();

    long getQuantity();

    byte getDecimals();

    boolean isReissuable();
}
