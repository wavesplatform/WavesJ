package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;

public interface AliasTransaction extends Transaction, Signable, WithId {
    static final byte ALIAS = 10;
    Alias getAlias();
}
